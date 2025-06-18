package dev.sleypner.asparser.controllers.pages.admin;

import dev.sleypner.asparser.dto.ElementOptions;
import dev.sleypner.asparser.dto.Form;
import dev.sleypner.asparser.dto.FormElement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPageController {
    @GetMapping("")
    String getDashboard(Model model) {

        model.addAttribute("loc", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/users")
    String getUsers(Model model) {

        model.addAttribute("loc", "users");

        return "admin/users";
    }

    @GetMapping("/settings")
    String getSettings(Model model) {

        List<FormElement> formElements = new ArrayList<>();
        formElements.add(new FormElement("primaryColor", "Primary color", "color", "#66CC00"));
        formElements.add(new FormElement("secondaryColor", "Secondary color", "color", "#CCFF00"));
        formElements.add(new FormElement("accentColor", "Accent color", "color", "#4da300"));
        formElements.add(new FormElement("textMainColorDark", "Text color dark", "color", "#4da300"));
        formElements.add(new FormElement("textMainColorLight", "Text color light", "color", "#4da300"));
        formElements.add(new FormElement("textSecondaryColorDark", "Text secondary color dark", "color", "#4da300"));
        formElements.add(new FormElement("bodyColor", "Body color", "color", "#4da300"));

        List<ElementOptions> fontsFamilyOptions = new ArrayList<>();
        fontsFamilyOptions.add(new ElementOptions("'Segoe UI', Tahoma, Geneva, Verdana", "'Segoe UI', Tahoma, Geneva, Verdana", false));
        fontsFamilyOptions.add(new ElementOptions("'Segoe UI', Tahoma, Geneva, Verdana", "'Segoe UI', Tahoma, Geneva, Verdana", false));
        fontsFamilyOptions.add(new ElementOptions("'Segoe UI', Tahoma, Geneva, Verdana", "'Segoe UI', Tahoma, Geneva, Verdana", false));
        fontsFamilyOptions.add(new ElementOptions("'Segoe UI', Tahoma, Geneva, Verdana", "'Segoe UI', Tahoma, Geneva, Verdana", false));
        fontsFamilyOptions.add(new ElementOptions("'Segoe UI', Tahoma, Geneva, Verdana", "'Segoe UI', Tahoma, Geneva, Verdana", false));
        formElements.add(FormElement.builder()
                .name("fontFamily")
                .label("Font family")
                .type("select")
                .options(fontsFamilyOptions)
                .build());

        List<ElementOptions> fontsSizeOptions = new ArrayList<>();
        fontsSizeOptions.add(new ElementOptions("Small", "Small"));
        fontsSizeOptions.add(new ElementOptions("Normal", "Normal", true));
        fontsSizeOptions.add(new ElementOptions("Big", "Big"));
        formElements.add(FormElement.builder()
                .name("fontSize")
                .label("Font size")
                .type("select")
                .options(fontsSizeOptions)
                .build());

        List<ElementOptions> dateFormatOptions = new ArrayList<>();
        dateFormatOptions.add(new ElementOptions("MM/DD/YYYY", "MM/DD/YYYY"));
        dateFormatOptions.add(new ElementOptions("DD/MM/YYYY", "DD/MM/YYYY"));
        dateFormatOptions.add(new ElementOptions("YYYY-MM-DD", "YYYY-MM-DD"));
        dateFormatOptions.add(new ElementOptions("DD MM YYYY", "DD MM YYYY"));
        formElements.add(FormElement.builder()
                .name("dateFormat")
                .label("Date format")
                .type("select")
                .options(dateFormatOptions)
                .build());


        Form form = Form.builder()
                .elements(formElements)
                .name("settingsForm")
                .type("form")
                .title("Settings")
                .button(true)
                .build();

        List<Form> forms = new ArrayList<>();
        forms.add(form);
        forms.add(form);
        forms.add(form);
        forms.add(form);
        model.addAttribute("form", forms);
        model.addAttribute("loc", "settings");

        return "admin/settings";
    }

    @GetMapping("/statistics")
    String getStatistics(Model model) {

        model.addAttribute("loc", "statistics");

        return "admin/statistics";
    }

    @GetMapping("/logs")
    String getLogs(Model model) {

        model.addAttribute("loc", "logs");

        return "admin/logs";
    }
}
