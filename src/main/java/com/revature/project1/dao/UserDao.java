package com.revature.project1.dao;

import com.revature.project1.model.Item;
import com.revature.project1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User,Integer> {
    public User findByUsername(String username);
    public Optional<User> findByUsernameAndPassword(String username, String password);
    public User findById(int userId);

//    @Query("update User u set u.cartContents =:items where u.userId =:userid")
//    public void saveUserCartContents(@Param("userid") int userId, @Param("items") List<Item> items);
//    @Query("select u from User u")
//    public List<Item> getCartById(int userId);
}
