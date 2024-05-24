package lv.tsi.bookstore.feature.audit

import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.theme.lumo.LumoUtility
import jakarta.annotation.security.PermitAll
import lv.tsi.bookstore.common.MainLayout
import lv.tsi.bookstore.feature.book.Book
import lv.tsi.bookstore.feature.book.BookService
import lv.tsi.bookstore.feature.user.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@PermitAll
@Route("audit", layout = MainLayout::class)
@PageTitle("Audit | Bookstore IMS")
class AuditView(
    bookService: BookService,
    private val auditService: AuditService,
) : VerticalLayout() {

    private val typePicker = ComboBox<AuditType>().apply {
        setItems(AuditType.entries)
        setItemLabelGenerator { it.toDisplayName() }
        addValueChangeListener { refreshGrid() }
    }

    private val datePicker = DatePicker().apply {
        addValueChangeListener { refreshGrid() }
    }

    private val form = AuditForm(bookService.findAll()).apply {
        addCloseListener { closeForm() }
        addSaveListener { onSave(it.audit) }
        width = "50em"
    }

    private fun onSave(audit: Audit) {
        try {
            auditService.create(audit)
            Notification.show("Audit has been added successfully!")
            form.clear()
        } catch (e: InsufficientStockException) {
            Notification.show(e.message)
        }

        refreshGrid()
        closeForm()
    }

    private val grid = Grid(AuditRowData::class.java).apply {
        setSizeFull()
        setColumns()

        addColumn("id")
            .setSortable(true)
            .setHeader("Audit")

        addColumn { it.createdOn.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) }
            .setSortable(true)
            .setHeader("Date")

        addColumn { it.createdOn.format(DateTimeFormatter.ofPattern("HH:mm:ss")) }
            .setSortable(true)
            .setHeader("Time")

        addColumn { it.type.toDisplayName() }
            .setSortable(true)
            .setHeader("Type")

        addColumn { it.book.isbn }
            .setSortable(true)
            .setHeader("ISBN")

        addColumn { it.book.title }
            .setSortable(true)
            .setHeader("Title")

        addColumn("quantity").apply {
            textAlign = ColumnTextAlign.CENTER
            setRenderer(ComponentRenderer { it ->
                val sign = if (it.type.decrease) "-" else "+"
                val theme = if (it.type.decrease) "error" else "success"
                Span(Text("$sign${it.quantity}")).apply {
                    element.setAttribute("theme", "badge $theme")
                }
            })
        }

        addColumn { it.createdBy?.username }
            .setSortable(true)
            .setHeader("Created By")

        columns.forEach { column ->
            column.setAutoWidth(true)
        }
    }

    init {
        setSizeFull()
        refreshGrid()

        val button = Button("Add Audit") {
            grid.asSingleSelect().clear()
            openForm()
        }

        add(FlexLayout(button, typePicker, datePicker).apply {
            setWidthFull()
            flexWrap = FlexLayout.FlexWrap.WRAP
            addClassName(LumoUtility.Gap.MEDIUM)
        })

        add(HorizontalLayout(grid, form).apply {
            setSizeFull()
            setFlexGrow(2.0, grid)
            setFlexGrow(1.0, form)
        })

        closeForm()
    }

    private fun refreshGrid() {
        val audits = auditService.findAll(AuditQuery(
            dateTerm = datePicker.value,
            typeTerm = typePicker.value,
        ))

        val records = audits.flatMap { audit ->
            audit.entries.map { entry ->
                val book = entry.book ?: error("Expected a non-null book.")
                AuditRowData(
                    id = audit.id,
                    book = book,
                    type = audit.type,
                    quantity = entry.quantity,
                    createdOn = audit.createdOn,
                    createdBy = audit.createdBy,
                )
            }
        }

        grid.setItems(records)
    }

    private fun openForm() {
        form.isVisible = true
    }

    private fun closeForm() {
        form.isVisible = false
    }
}

private class AuditForm(books: List<Book>) : FormLayout() {

    private val binder = BeanValidationBinder(Audit::class.java)
    private val type = ComboBox<AuditType>("Type").apply {
        setItems(AuditType.entries)
        setItemLabelGenerator { it.toDisplayName() }
    }

    private val entries = mutableListOf<AuditEntry>()
    private val entriesLayout = VerticalLayout()

    private val entryBinder = BeanValidationBinder(AuditEntry::class.java)
    private val entryQuantity = IntegerField()
    private val entryBook = ComboBox<Book>().apply {
        setItems(books)
        setItemLabelGenerator { "${it.isbn}: ${it.title}" }
    }

    private val entrySave = Button(VaadinIcon.PLUS.create()) {
        if (binder.isValid) {
            val entryISBN = entryBook.value?.isbn ?: error("Expected a non-null book.")

            val existingEntry = entries.find { entryISBN == it.book?.isbn }
            if (existingEntry == null) {
                entries.add(entryBinder.bean)
                save.isEnabled = entries.isNotEmpty()
            } else {
                existingEntry.quantity += entryQuantity.value
            }

            entriesLayout.removeAll()
            entries.forEach { entry ->
                val layout = HorizontalLayout()

                val previewBook = TextField().apply {
                    value = "[${entry.book?.isbn}] ${entry.book?.title}"
                    isReadOnly = true
                }
                val previewQuantity = IntegerField().apply {
                    value = entry.quantity
                    isReadOnly = true
                }

                val removeButton = Button(VaadinIcon.TRASH.create()).apply {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR)
                    addClickListener {
                        entries.remove(entry)
                        entriesLayout.remove(layout)
                        save.isEnabled = entries.isNotEmpty()
                    }
                }

                layout.add(previewBook)
                layout.add(previewQuantity)
                layout.add(removeButton)
                layout.setWidthFull()
                layout.expand(previewBook)

                entriesLayout.add(layout)
            }

            entryBinder.bean = AuditEntry()
        }
    }

    val save = Button("Save").apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        addClickListener {
            if (!binder.isValid || entries.isEmpty()) {
                return@addClickListener
            }

            binder.bean.entries.addAll(entries)
            fireEvent(SaveEvent(this@AuditForm, binder.bean))
        }
    }

    val close = Button("Close") {
        fireEvent(CloseEvent(this@AuditForm))
    }

    init {
        binder.bean = Audit()
        binder.bind(type, "type")
        binder.addStatusChangeListener { save.isEnabled = binder.isValid }

        entryBinder.bean = AuditEntry()
        entryBinder.addStatusChangeListener { entrySave.isEnabled = entryBinder.isValid }
        entryBinder.bind(entryBook, "book")
        entryBinder.bind(entryQuantity, "quantity")

        entriesLayout.isPadding = false
        entriesLayout.isSpacing = false
        entriesLayout.addClassNames(LumoUtility.Padding.Bottom.SMALL)

        save.isEnabled = false

        val entryLayout = HorizontalLayout(entryBook, entryQuantity, entrySave).apply { expand(entryBook) }
        add(type, entryLayout, entriesLayout, HorizontalLayout(save, close))
    }

    fun clear() {
        binder.bean = Audit()
        entries.clear()
        entriesLayout.removeAll()
        save.isEnabled = false
    }

    fun addSaveListener(listener: ComponentEventListener<SaveEvent>): Registration {
        return addListener(SaveEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<CloseEvent>): Registration {
        return addListener(CloseEvent::class.java, listener)
    }
}

private class SaveEvent(
    source: AuditForm, val audit: Audit,
) : ComponentEvent<AuditForm>(source, false)

private class CloseEvent(
    source: AuditForm,
) : ComponentEvent<AuditForm>(source, false)

data class AuditRowData(
    var id: Long,
    var book: Book,
    var quantity: Int,
    var type: AuditType,
    var createdOn: LocalDateTime,
    var createdBy: User?,
)
