package lv.tsi.bookstore.common

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.theme.lumo.LumoUtility
import lv.tsi.bookstore.feature.audit.AuditView
import lv.tsi.bookstore.feature.book.BookView
import lv.tsi.bookstore.feature.login.SecurityService
import lv.tsi.bookstore.feature.user.Role
import lv.tsi.bookstore.feature.user.UserView
import lv.tsi.bookstore.feature.user.hasRole

/*
    MainLayout:
        WMC: 1
        CBO: 3
        RFC: 5
        LCOM4: 1

    Header:
        WMC: 1
        CBO: 5
        RFC: 10
        LCOM4: 1

    Drawer:
        WMC: 1
        CBO: 6
        RFC: 8
        LCOM4: 1
*/

class MainLayout(
    securityService: SecurityService,
) : AppLayout() {

    init {
        addToNavbar(Header(securityService))
        addToDrawer(Drawer(securityService))
        primarySection = Section.DRAWER
    }
}

private class Header(securityService: SecurityService) : HorizontalLayout() {

    init {
        val logo = H1("Bookstore IMS").apply {
            addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM,
            )
        }

        val logout = Button("Log Out") {
            securityService.logout()
        }

        add(DrawerToggle(), logo, logout)

        expand(logo)
        setWidthFull()

        defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM,
        )
    }
}

private class Drawer(securityService: SecurityService) : Div() {

    init {
        val hasManagerAccess = securityService
            .getAuthenticatedUserDetails()
            .hasRole(Role.MANAGER)

        add(SideNav().apply {
            label = "Bookstore"
            addItem(SideNavItem("Inventory", BookView::class.java, VaadinIcon.DATABASE.create()))
            addItem(SideNavItem("Audit", AuditView::class.java, VaadinIcon.ARCHIVE.create()))
        })

        if (hasManagerAccess) {
            add(SideNav().apply {
                label = "Management"
                addItem(SideNavItem("Users", UserView::class.java, VaadinIcon.USERS.create()))
            })
        }
    }
}
