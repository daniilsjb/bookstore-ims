package lv.tsi.bookstore.feature.user

import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.theme.lumo.LumoUtility
import jakarta.annotation.security.RolesAllowed
import lv.tsi.bookstore.common.MainLayout
import lv.tsi.bookstore.feature.security.SecurityService

@RolesAllowed("MANAGER")
@Route("users", layout = MainLayout::class)
@PageTitle("Users | Bookstore IMS")
class UserView(
    private val userService: UserService,
    private val securityService: SecurityService,
) : VerticalLayout() {

    private val search = TextField().apply {
        placeholder = "Search..."
        valueChangeMode = ValueChangeMode.LAZY
        isClearButtonVisible = true
        addValueChangeListener { refreshGrid() }
    }

    private val form = UserForm().apply {
        width = "25em"
        addCloseListener { closeForm() }
        addSaveListener { event ->
            val user = event.user
            try {
                userService.create(user)
                refreshGrid()
                closeForm()
                Notification.show("User '${user.username}' has been added successfully!")
            } catch (e: DuplicateUserException) {
                Notification.show(e.message)
            }
        }
    }

    private val grid = Grid(User::class.java).apply {
        setSizeFull()
        setColumns("username", "email", "role", "active")

        // Ensure that columns take up necessary width even on small screens.
        columns.forEach { column -> column.setAutoWidth(true) }

        // Customize the user status column.
        getColumnByKey("active").apply {
            setHeader("Status")
            setRenderer(ComponentRenderer { user: User ->
                Span().apply {
                    if (user.active) {
                        element.setAttribute("theme", "badge success")
                        add("Active")
                    } else {
                        element.setAttribute("theme", "badge error")
                        add("Locked")
                    }
                }
            })
        }

        // Add the action column for user locking and unlocking.
        addComponentColumn { user ->
            // Select an appropriate icon based on current status.
            val icon = if (user.active) {
                VaadinIcon.LOCK.create()
            }  else {
                VaadinIcon.UNLOCK.create()
            }

            // Create a lock-toggling button.
            Button(icon).apply {
                // Ensure that a user cannot block himself.
                isEnabled = securityService.getAuthenticatedUserDetails().username != user.username

                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                addClickListener {
                    userService.toggle(user)
                    refreshGrid()
                }
            }
        }.apply {
            setTextAlign(ColumnTextAlign.END)
        }
    }

    init {
        setSizeFull()
        refreshGrid()

        val button = Button("Add User") {
            grid.asSingleSelect().clear()
            openForm(User())
        }
        add(HorizontalLayout(button, search).apply {
            setWidthFull()
            expand(search)
        })

        add(HorizontalLayout(grid, form).apply {
            setSizeFull()
            setFlexGrow(2.0, grid)
            setFlexGrow(1.0, form)
        })

        closeForm()
    }

    private fun refreshGrid() {
        grid.setItems(userService.findAll(search.value))
    }

    private fun openForm(user: User) {
        form.setUser(user)
        form.isVisible = true
    }

    private fun closeForm() {
        form.setUser(null)
        form.isVisible = false
    }
}

private class UserForm : FormLayout() {

    private val username = TextField("Username")
    private val password = TextField("Password")
    private val email = TextField("Email")
    private val role = ComboBox<Role>("Role").apply { setItems(Role.entries) }

    private val save = Button("Save").apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
        addClickListener {
            if (binder.isValid) {
                fireEvent(SaveEvent(this@UserForm, binder.bean))
            }
        }
    }

    private val close = Button("Close").apply {
        addClickShortcut(Key.ESCAPE)
        addClickListener {
            fireEvent(CloseEvent(this@UserForm))
        }
    }

    private val binder = BeanValidationBinder(User::class.java)

    init {
        binder.bindInstanceFields(this)
        binder.addStatusChangeListener { save.isEnabled = binder.isValid }

        add(username, password, email, role)
        add(HorizontalLayout(save, close).apply {
            addClassNames(LumoUtility.Padding.Vertical.MEDIUM)
        })
    }

    fun setUser(user: User?) {
        binder.bean = user
    }

    fun addSaveListener(listener: ComponentEventListener<SaveEvent>): Registration {
        return addListener(SaveEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<CloseEvent>): Registration {
        return addListener(CloseEvent::class.java, listener)
    }
}

private class SaveEvent(
    source: UserForm, val user: User
) : ComponentEvent<UserForm>(source, false)

private class CloseEvent(
    source: UserForm
) : ComponentEvent<UserForm>(source, false)
