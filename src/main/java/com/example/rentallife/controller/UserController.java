package com.example.rentallife.controller;

import com.example.rentallife.dto.PropertyDTO;
import com.example.rentallife.dto.UserDTO;
import com.example.rentallife.entity.Property;
import com.example.rentallife.entity.User;
import com.example.rentallife.entity.UserType;
import com.example.rentallife.service.PropertyService;
import com.example.rentallife.service.UserService;
import com.example.rentallife.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        return "redirect:/assign-tenant";
    }
    @GetMapping("/dashboard")
    public String getDashboard(Model model, Authentication authentication) {
        String currentUserName = authentication.getName();
        User currentUser = userService.findUserByName(currentUserName);



        // 添加空值检查
        if (currentUser == null) {
            // 如果找不到用户，重定向到登录页面或显示错误信息
            return "redirect:/login?error=user_not_found";
        }
        System.out.println("Current User Type: " + currentUser.getUserType());

        if (currentUser.getUserType() == UserType.LANDLORD) {
            List<Property> properties = propertyService.getPropertiesByLandlord(currentUser);
            // 创建一个 Map，将房产ID与对应的租客列表关联起来
            Map<Long, List<User>> propertyTenantsMap = new HashMap<>();
            for (Property property : properties) {
                List<User> tenants = property.getTenants();  // 假设你在 Property 类中有 getTenants 方法
                if (tenants != null) {
                    // 对租客列表去重
                    tenants = tenants.stream().distinct().collect(Collectors.toList());
                } else {
                    tenants = new ArrayList<>();
                }
                propertyTenantsMap.put(property.getId(), tenants);
            }
            // 将 propertyTenantsMap 添加到 model 中
            model.addAttribute("properties", properties);
            model.addAttribute("propertyTenantsMap", propertyTenantsMap);
            System.out.println("Properties found: " + properties.size());
            return "landlord-dashboard";

        } else if (currentUser.getUserType() == UserType.TENANT) {
            // 这里处理租客的逻辑
            return "home";
        } else {
            return "redirect:/access-denied";
        }
    }
    @GetMapping("/assign-tenant")
    public String showAssignTenantForm(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("tenants", userService.getAllTenants());
        return "assign-tenant";
    }
    @PostMapping("/assign-tenant")
    public String assignTenant(@RequestParam("propertyId") Long propertyId, @RequestParam("tenantId") Long tenantId) {
        propertyService.addTenantToProperty(propertyId, tenantId);
        return "redirect:/dashboard";
    }

    @PostMapping("/delete-property/{id}")
    public String deleteProperty(@PathVariable("id") Long propertyId, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        Property property = propertyService.findPropertyById(propertyId);

        if (property.getLandlord().getId().equals(currentUser.getId())) {
            propertyService.deletePropertyById(propertyId);
            return "redirect:/dashboard";
        } else {
            return "redirect:/access-denied"; // 如果用户不是物业的所有者，重定向到拒绝访问页面
        }
    }

}
