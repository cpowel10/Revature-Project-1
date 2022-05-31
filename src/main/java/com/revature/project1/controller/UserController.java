package com.revature.project1.controller;

import com.revature.project1.annotations.Authorized;
import com.revature.project1.model.Cart;
import com.revature.project1.model.Role;
import com.revature.project1.model.User;
import com.revature.project1.services.AuthorizationService;
import com.revature.project1.services.CartService;
import com.revature.project1.services.ItemService;
import com.revature.project1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired()
    User user;

    @Autowired
    Cart cart;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Autowired
    CartService cartService;

    @Autowired
    private AuthorizationService authorizationService;

    boolean result;

    @PostMapping("/register") //localhost:8088/register
    public ResponseEntity<String> register(@RequestBody User user){
        if(user == null){
            return new ResponseEntity<String>("User is null",HttpStatus.NO_CONTENT);
        }
        if(userService.isUserExists(user.getUserId())){
//            responseEntity = new ResponseEntity<String>
//                    ("Cannot save because user with user id : "
//                            + user.getUserId() + " already exists", HttpStatus.CONFLICT);   //409
            return new ResponseEntity<String>
                    ("Cannot save because user with user id : "
                            + user.getUserId() + " already exists", HttpStatus.CONFLICT);   //409
        }
        else{
            return ResponseEntity.accepted().body("Successfully registered user: "
                    +userService.register(user).toString());
        }
    }

    @PutMapping("/update") //localhost:8088/update
    @Authorized(allowedRoles = {Role.ADMIN,Role.CUSTOMER,Role.EMPLOYEE})
    public ResponseEntity<String> updateUserInfo(@RequestBody User user){
        authorizationService.guardByUserId(user.getUserId());

        User result = userService.update(user);
        return ResponseEntity.accepted().body("Successfully updated user: "+result.toString());
    }

    @PostMapping("/login/{user}/{pass}") //localhost:8088/login/user/pass
    public ResponseEntity<String> login(@PathVariable("user") String username, @PathVariable("pass") String password){
        User u = userService.login(username,password);
        return new ResponseEntity<String>("Welcome "+u.getFirstName()+" "+u.getLastName(),HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        userService.logout();
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/getusersandcarts")  //localhost:8088/getusersandcarts
    @Authorized(allowedRoles = {Role.ADMIN})
    public ResponseEntity<String> getUsersAndCarts(){
        //String userCartString = userService.getUsersAndCarts();
        return ResponseEntity.ok(userService.getUsersAndCarts());
    }

    @GetMapping("/getmycart/{userid}")  // localhost:8088/getmycart/{userid}
    @Authorized(allowedRoles = {Role.ADMIN,Role.CUSTOMER,Role.EMPLOYEE})
    public ResponseEntity<String> getMyCart(@PathVariable("userid") int userId){
        authorizationService.guardByUserId(userId);

        return ResponseEntity.ok(userService.getSingleUserAndCart(userId));
    }

    @PutMapping("/addproducttocart/{userid}/{itemid}") //localhost:8088/addproducttocart/userid/itemId
    @Authorized(allowedRoles = {Role.ADMIN,Role.CUSTOMER,Role.EMPLOYEE})
    public ResponseEntity<String> addProductToCart(@PathVariable("userid") int userId,@PathVariable("itemid") int itemId ){
        authorizationService.guardByUserId(userId);
        ResponseEntity<String> responseEntity = null;
        user = userService.getUser(userId);
        if(user==null){
            responseEntity = new ResponseEntity<String>
                    ("Invalid username or password. Try again",HttpStatus.NOT_ACCEPTABLE);
        }
        else{
            result = userService.addItemToCart(user,itemId);
            System.out.println("Cart Contents after adding: "+user.getCartContents());
            if(!result){
                responseEntity = new ResponseEntity<String>
                        ("Unable to add item to cart",HttpStatus.CONFLICT);
            }
            else{
                System.out.println("Inside UserController: "+user.getCartContents());
                responseEntity = new ResponseEntity<String>
                        ("Successfully added item to cart",HttpStatus.OK);
            }
        }

        return responseEntity;
    }

    @DeleteMapping("/deleteuser/{userid}") //localhost:8088/deleteuser/userid
    @Authorized(allowedRoles = {Role.ADMIN,Role.CUSTOMER,Role.EMPLOYEE})
    public ResponseEntity<String> deleteUser(@PathVariable("userid") int userId){
        authorizationService.guardByUserId(userId);
        ResponseEntity<String> responseEntity = null;
        if(userService.isUserExists(userId)){
            result = userService.deleteAccount(userId);
            responseEntity = new ResponseEntity<String>
                    ("Successfully deleted user",HttpStatus.OK);
        }
        else{
            responseEntity = new ResponseEntity<String>
                    ("Unable to delete user because user id: "+userId+" is invalid"
                            ,HttpStatus.NOT_ACCEPTABLE);
        }

        return responseEntity;
    }

    @GetMapping("/getitemsinstock") //localhost:8088/getitemsinstock
    public ResponseEntity<String> getItemsInStock(){
        ResponseEntity<String> responseEntity = null;
        String items = itemService.getAllInstockItems();
        if(items==""){
            return new ResponseEntity<String>
                    ("No items instock. Please come back later",HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<String>(items,HttpStatus.OK);
        }
    }
}
