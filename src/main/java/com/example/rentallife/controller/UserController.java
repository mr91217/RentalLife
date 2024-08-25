package com.example.rentallife.controller;

import com.example.rentallife.dto.PropertyDTO;
import com.example.rentallife.dto.UserDTO;
import com.example.rentallife.entity.*;
import com.example.rentallife.service.MaintenanceRequestService;
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

import java.time.LocalDateTime;
import java.util.*;
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
    public String getHome(Authentication authentication) {
        // 获取当前用户名
        String currentUserName = authentication.getName();
        // 查找当前用户
        User currentUser = userService.findUserByName(currentUserName);

        // 添加空值检查
        if (currentUser == null) {
            // 如果找不到用户，重定向到登录页面或显示错误信息
            return "redirect:/login?error=user_not_found";
        }

        // 根据用户类型进行重定向
        if (currentUser.getUserType() == UserType.LANDLORD) {
            // 如果用户是房东，重定向到 dashboard
            return "redirect:/dashboard";
        } else if (currentUser.getUserType() == UserType.TENANT) {
            // 如果用户是租客，返回 home 页面
            log.info("Home page displayed");
            return "home";
        } else {
            // 如果用户的类型不匹配，重定向到拒绝访问页面
            return "redirect:/access-denied";
        }
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
//        // 获取所有房东用户
//        List<User> landlords = userService.getAllLandlords();
//
//        // 将房东列表添加到模型中
//        model.addAttribute("landlords", landlords);

        // 初始化 PropertyDTO 对象并设置默认值
        PropertyDTO propertyDTO = new PropertyDTO();
        model.addAttribute("propertyDTO", propertyDTO);
        return "property-form";
    }
    @PostMapping("/property-process")
    public String processProperty(@Valid @ModelAttribute("propertyDTO") PropertyDTO propertyDTO, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "property-form";
        }
        // 获取当前登录的房东用户
        User currentLandlord = userService.findUserByName(authentication.getName());

        // 将当前房东设置为物业的房东
        propertyDTO.setLandlordId(currentLandlord.getId());
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
    public String showAssignTenantForm(Model model, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());

        // 获取当前房东的房产列表
        List<Property> landlordProperties = propertyService.getPropertiesByLandlord(currentUser);

        model.addAttribute("properties", landlordProperties);
        model.addAttribute("tenants", userService.getAllTenants());
        return "assign-tenant";
    }
    @PostMapping("/assign-tenant")
    public String assignTenant(@RequestParam("propertyId") Long propertyId, @RequestParam(value = "tenantId", required = false) Long tenantId) {
        if (tenantId != null) {
            // 如果指定了租户ID，分配租户
            propertyService.addTenantToProperty(propertyId, tenantId);
        } else {
            // 如果tenantId是null，移除当前所有租户
            propertyService.removeTenantsFromProperty(propertyId);
        }
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
    @GetMapping("/edit-property/{id}")
    public String showEditPropertyForm(@PathVariable("id") Long propertyId, Model model, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        Property property = propertyService.findPropertyById(propertyId);

        if (property.getLandlord().getId().equals(currentUser.getId())) {
            model.addAttribute("property", property);
            model.addAttribute("tenants", userService.getAllTenants()); // 获取所有租客
            return "edit-property";
        } else {
            return "redirect:/access-denied"; // 如果用户不是物业的所有者，重定向到拒绝访问页面
        }
    }
    @PostMapping("/update-property/{id}")
    public String updateProperty(@PathVariable("id") Long propertyId,
                                 @RequestParam("address") String address,
                                 @RequestParam("city") String city,
                                 @RequestParam("state") String state,
                                 @RequestParam("zip") String zip,
                                 @RequestParam("rooms") int rooms,
                                 @RequestParam("price") double price,
                                 @RequestParam(value = "term", required = false) String term,
                                 @RequestParam(value = "tenantId", required = false) Long tenantId, // 允许tenantId为null,
                                 Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        Property property = propertyService.findPropertyById(propertyId);

        if (property.getLandlord().getId().equals(currentUser.getId())) {
            // 更新物业信息
            property.setAddress(address);
            property.setCity(city);
            property.setState(state);
            property.setZip(zip);
            property.setRooms(rooms);
            property.setPrice(price);
            property.setTerm(term);

            // 重新分配租户
            if (tenantId != null) {
                User newTenant = userService.findUserById(tenantId);
                property.setTenants(Collections.singletonList(newTenant)); // 单一租户
            } else {
                property.setTenants(new ArrayList<>()); // 移除所有租户
            }

            propertyService.saveProperty(property); // 保存修改后的物业信息
            return "redirect:/dashboard"; // 更新成功后重定向到仪表板
        } else {
            return "redirect:/access-denied"; // 如果用户不是物业的所有者，重定向到拒绝访问页面
        }
    }
    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @GetMapping("/submit-request")
    public String showSubmitRequestForm(Model model, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        List<Property> properties = propertyService.getPropertiesByTenant(currentUser);

        model.addAttribute("properties", properties);
        model.addAttribute("maintenanceRequest", new MaintenanceRequest());
        return "submit-request-form";
    }

    @PostMapping("/submit-request")
    public String submitRequest(MaintenanceRequest maintenanceRequest, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        maintenanceRequest.setTenant(currentUser);
        maintenanceRequest.setSubmittedAt(LocalDateTime.now());
        maintenanceRequestService.submitRequest(maintenanceRequest);
        return "redirect:/home";
    }
    @GetMapping("/maintenance-requests")
    public String showMaintenanceRequests(Model model, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        List<MaintenanceRequest> requests = maintenanceRequestService.getRequestsForLandlord(currentUser);
        model.addAttribute("requests", requests);
        return "maintenance-requests";
    }
    @PostMapping("/update-request-status")
    public String updateRequestStatus(@RequestParam("requestId") Long requestId, @RequestParam("status") RequestStatus status) {
        MaintenanceRequest request = maintenanceRequestService.findById(requestId);
        request.setStatus(status);
        maintenanceRequestService.save(request);
        return "redirect:/maintenance-requests"; // 更改状态后重定向回请求列表页面
    }
    @GetMapping("/maintenance-status")
    public String showMaintenanceStatus(Model model, Authentication authentication) {
        User currentUser = userService.findUserByName(authentication.getName());
        List<MaintenanceRequest> requests = maintenanceRequestService.getRequestsForTenant(currentUser);
        model.addAttribute("requests", requests);
        return "maintenance-status-t";
    }

}
