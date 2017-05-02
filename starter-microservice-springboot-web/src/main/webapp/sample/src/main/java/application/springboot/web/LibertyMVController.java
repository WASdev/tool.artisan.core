package application.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LibertyMVController {

    @RequestMapping("/springbootmvc")
    public String greeting(@RequestParam(value="entity", required=false, defaultValue="World") String entity, Model model) {
        model.addAttribute("entity", entity);
        return "hello";
    }

}
