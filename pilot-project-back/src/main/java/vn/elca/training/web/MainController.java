package vn.elca.training.web;

//import jdk.tools.jmod.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import vn.elca.training.service.ProjectService;

/**
 * @author vlp
 *
 */
@RestController
public class MainController extends AbstractApplicationController {

    private ProjectService projectService;

    @Value("${application.title}")
    private String title;
    
    //Error3: No autowire
    @Autowired
    public MainController(@Qualifier("projectServiceImpl") ProjectService projectService) {
        this.projectService=projectService;
    }

    @Value("${application.message}")
    private String message;

    @GetMapping("/main")
    public String main() {
        return title + ". " + String.format(message, projectService.count());
    }
}
