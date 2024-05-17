package lv.tsi.bookstore.feature.security

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("login")
@AnonymousAllowed
@PageTitle("Login | Bookstore IMS")
class LoginView : VerticalLayout(), BeforeEnterObserver {

    private val login = LoginForm().apply {
        isForgotPasswordButtonVisible = false
        action = "login"
    }

    init {
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        add(H1("Bookstore IMS"), login)
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        login.isError = event.location
            .queryParameters
            .parameters
            .containsKey("error")
    }
}
