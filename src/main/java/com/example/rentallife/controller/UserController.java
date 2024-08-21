package com.example.rentallife.controller;

import com.example.rentallife.dto.PropertyDTO;
import com.example.rentallife.dto.UserDTO;
import com.example.rentallife.entity.User;
import com.example.rentallife.service.PropertyService;
import com.example.rentallife.service.UserService;
import com.example.rentallife.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@Slf4j
public class UserController {

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new
                StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private UserServiceImpl userDetailsService;
    private final PropertyService propertyService;
    @Autowired
    public UserController(UserServiceImpl userDetailsService, PropertyService propertyService) {
        this.userDetailsService = userDetailsService;
        this.propertyService = propertyService;
    }
    @GetMapping("/")
    private String redirectToLogin()
    {
        return "redirect:/login";
    }
    @GetMapping("/sign-up")
    public String signUp(Model model)
    {
        model.addAttribute("userDto", new UserDTO());
        return "sign-up";
    }
    @PostMapping("/signup-process")
    public String signupProcess(@Valid @ModelAttribute ("userDto") UserDTO
                                        userDTO, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
        {
            log.warn("Wrong attempt");
            return "sign-up";
        }
        userDetailsService.creat(userDTO,userDTO.getUserType());
        return "confirmation";
    }
    /**
     * In order to make code more readable  it is good practice to
     * use special DTOs for login It also make controllers
     * less dependent from entities and separate validation from
     * jpa functionality
     */
    @GetMapping("/login")
    public String getLoginPage()
    {
        log.info("Login page displayed");
        return "login";
    }
    @RequestMapping("/home")
    public String getHome()
    {
        log.info("home page displayed");
        return "home";
    }
    @GetMapping("/claim")
    public String showClaimPage() {
        return "claim";
    }
    @GetMapping("/payment")
    public String showPaymentPage() {
        return "payment";
    }
    @Autowired
    private UserService userService;
    @GetMapping("/property-form")
    public String showPropertyForm(Model model) {
        // 获取所有房东用户
        List<User> landlords = userService.getAllLandlords();

        // 将房东列表添加到模型中
        model.addAttribute("landlords", landlords);

        // 初始化 PropertyDTO 对象并设置默认值
        PropertyDTO propertyDTO = new PropertyDTO();
        model.addAttribute("propertyDTO", propertyDTO);
        return "property-form";
    }
    @PostMapping("/property-process")
    public String processProperty(@Valid @ModelAttribute("propertyDTO") PropertyDTO propertyDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "property-form";
        }
        // 保存房产信息的逻辑
        propertyService.addProperty(propertyDTO);
        return "confirmation";
    }

}
