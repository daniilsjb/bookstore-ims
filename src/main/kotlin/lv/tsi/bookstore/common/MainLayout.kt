package lv.tsi.bookstore.common

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.theme.lumo.LumoUtility
import lv.tsi.bookstore.feature.audit.AuditView
import lv.tsi.bookstore.feature.book.BookView
import lv.tsi.bookstore.feature.security.SecurityService
import lv.tsi.bookstore.feature.user.isManager
import lv.tsi.bookstore.feature.user.UserView

class MainLayout(
    private val securityService: SecurityService,
) : AppLayout() {

    // Initialize the application's header.
    init {
        val logo = H1("Bookstore IMS")
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM,
        )

        val logout = Button("Log Out") {
            securityService.logout()
        }

        val header = HorizontalLayout(DrawerToggle(), logo, logout)
        header.defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        header.expand(logo)
        header.setWidthFull()
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM,
        )

        addToNavbar(header)
    }

    // Initialize the application's drawer.
    init {
        val hasManagerAccess = securityService
            .getAuthenticatedUserDetails()
            .isManager()

        addToDrawer(SideNav().apply {
            label = "Bookstore"
            addItem(SideNavItem("Inventory", BookView::class.java, VaadinIcon.DATABASE.create()))
            addItem(SideNavItem("Audit", AuditView::class.java, VaadinIcon.ARCHIVE.create()))
        })

        if (hasManagerAccess) {
            addToDrawer(SideNav().apply {
                label = "Management"
                addItem(SideNavItem("Users", UserView::class.java, VaadinIcon.USERS.create()))
            })
        }

        primarySection = Section.DRAWER
    }
}
