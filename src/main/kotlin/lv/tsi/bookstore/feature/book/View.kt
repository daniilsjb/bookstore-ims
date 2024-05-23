package lv.tsi.bookstore.feature.book

import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.theme.lumo.LumoUtility
import jakarta.annotation.security.PermitAll
import lv.tsi.bookstore.common.MainLayout
import lv.tsi.bookstore.feature.login.SecurityService
import lv.tsi.bookstore.feature.user.Role
import lv.tsi.bookstore.feature.user.hasRole

@PermitAll
@Route(value = "books", layout = MainLayout::class)
@RouteAlias(value = "", layout = MainLayout::class)
@PageTitle("Books | Bookstore IMS")
class BookView(
    private val bookService: BookService,
    private val securityService: SecurityService,
) : VerticalLayout() {

    private val search = TextField().apply {
        placeholder = "Search..."
        valueChangeMode = ValueChangeMode.LAZY
        isClearButtonVisible = true
        addValueChangeListener { refreshGrid() }
    }

    private val grid = Grid(Book::class.java).apply {
        setSizeFull()

        // Ensure that only managers can edit books.
        if (hasManagerAccess) {
            asSingleSelect().addValueChangeListener { event ->
                if (event.value != null) {
                    openForm(event.value)
                }
            }
        }

        setColumns("isbn", "title")
        getColumnByKey("isbn").setHeader("ISBN")

        addColumn { it.authors.map(Author::name).sorted().joinToString(separator = ", ") }
            .setSortable(true)
            .setHeader("Authors")

        addColumn { it.publisher?.name }
            .setSortable(true)
            .setHeader("Publisher")

        addColumns("quantity")
        getColumnByKey("quantity").setTextAlign(ColumnTextAlign.CENTER)

        addColumns("basePrice")
        getColumnByKey("basePrice").setTextAlign(ColumnTextAlign.CENTER)

        columns.forEach { it.setAutoWidth(true) }
    }

    private val form = BookForm(
        bookService.findAllPublishers(),
        bookService.findAllAuthors(),
    ).apply {
        width = "25em"

        addSaveListener { event ->
            val book = event.book
            try {
                when (mode) {
                    Mode.ADD -> bookService.create(book)
                    Mode.EDIT -> bookService.update(book)
                }

                refreshGrid()
                closeForm()

                Notification.show("Book '${book.title}' has been added successfully!")
            } catch (e: DuplicateBookException) {
                setIdentifierError("This ISBN is already taken")
            }
        }

        addDeleteListener { event ->
            val book = event.book
            try {
                bookService.delete(book)

                refreshGrid()
                closeForm()

                Notification.show("Book '${book.title}' removed successfully!")
            } catch (e: ReferencedBookException) {
                Notification.show("Book '${book.title}' cannot be removed.")
            }
        }

        addCloseListener {
            closeForm()
        }
    }


    private val hasManagerAccess: Boolean
        get() = securityService
            .getAuthenticatedUserDetails()
            .hasRole(Role.MANAGER)

    init {
        setSizeFull()
        refreshGrid()

        val button = Button("Add Book").apply {
            isVisible = hasManagerAccess
            addClickListener {
                grid.asSingleSelect().clear()
                openForm(Book())
            }
        }
        add(HorizontalLayout(button, search).apply {
            expand(search)
            setWidthFull()
        })

        add(HorizontalLayout(grid, form).apply {
            setSizeFull()
            setFlexGrow(2.0, grid)
            setFlexGrow(1.0, form)
        })

        closeForm()
    }

    private fun refreshGrid() {
        grid.setItems(bookService.findAll(search.value))
    }

    private fun openForm(book: Book) {
        form.setBook(book)
        form.isVisible = true
    }

    private fun closeForm() {
        form.setBook(null)
        form.isVisible = false
    }
}

private enum class Mode {
    ADD, EDIT
}

private class SaveEvent(
    source: BookForm, val book: Book
) : ComponentEvent<BookForm>(source, false)

private class DeleteEvent(
    source: BookForm, val book: Book
) : ComponentEvent<BookForm>(source, false)

private class CloseEvent(
    source: BookForm
) : ComponentEvent<BookForm>(source, false)

private class BookForm(
    publishers: List<Publisher>,
    authors: List<Author>,
) : FormLayout() {

    var mode: Mode = Mode.ADD
        private set(value) {
            field = value
            isbn.isEnabled = value == Mode.ADD
            delete.isVisible = value == Mode.EDIT
        }

    private val isbn = TextField("ISBN")
    private val title = TextField("Title")
    private val basePrice = BigDecimalField("Base Price")

    private val authors = MultiSelectComboBox<Author>("Author").apply {
        setItems(authors)
        setItemLabelGenerator { it.name }
    }

    private val publisher = ComboBox<Publisher>("Publisher").apply {
        setItems(publishers)
        setItemLabelGenerator { it.name }
    }

    private val quantity = IntegerField("Quantity").apply {
        helperText = "This value can only be updated via auditing."
        isEnabled = false
    }

    private val save = Button("Save").apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
        addClickListener {
            if (binder.isValid) {
                fireEvent(SaveEvent(this@BookForm, binder.bean))
            }
        }
    }

    private val delete = Button(Icon(VaadinIcon.TRASH)).apply {
        addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR)
        addClickListener {
            ConfirmDialog().apply {
                setHeader("Delete this book?")
                setText("Are you sure you want to permanently delete this item?")
                setCancelable(true)
                setConfirmText("Delete")
                setConfirmButtonTheme("error primary")
                addConfirmListener {
                    fireEvent(DeleteEvent(this@BookForm, binder.bean))
                }
            }.open()
        }
    }

    private val close = Button("Close").apply {
        addClickShortcut(Key.ESCAPE)
        addClickListener {
            fireEvent(CloseEvent(this@BookForm))
        }
    }

    private val binder = BeanValidationBinder(Book::class.java)

    init {
        binder.bindInstanceFields(this)
        binder.addStatusChangeListener { save.isEnabled = binder.isValid }

        val leftLayout = HorizontalLayout(save, close)
        val fullLayout = HorizontalLayout(leftLayout, delete).apply {
            addClassNames(LumoUtility.Padding.Vertical.MEDIUM)
            expand(leftLayout)
        }

        add(this.isbn, this.title, this.authors, this.publisher, this.basePrice, this.quantity)
        add(fullLayout)
    }

    fun setIdentifierError(message: String) {
        isbn.isInvalid = true
        isbn.errorMessage = message
    }

    fun setBook(book: Book?) {
        binder.bean = book
        if (book != null) {
            mode = if (book.isbn.isBlank()) Mode.ADD else Mode.EDIT
        }
    }

    fun addSaveListener(listener: ComponentEventListener<SaveEvent>): Registration {
        return addListener(SaveEvent::class.java, listener)
    }

    fun addDeleteListener(listener: ComponentEventListener<DeleteEvent>): Registration {
        return addListener(DeleteEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<CloseEvent>): Registration {
        return addListener(CloseEvent::class.java, listener)
    }
}
