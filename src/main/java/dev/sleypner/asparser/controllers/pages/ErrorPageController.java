package dev.sleypner.asparser.controllers.pages;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String renderErrorPage(HttpServletRequest httpRequest) {
        try {
            Integer statusCode = (Integer) httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            if (statusCode != null) {
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    return "errors/not-found";
                } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                    return "errors/access-denied";
                } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    return "errors/internal-error";
                }
            }
        } catch (Exception e) {
            return "errors/internal-error";
        }
        return "errors/internal-error";
    }

}