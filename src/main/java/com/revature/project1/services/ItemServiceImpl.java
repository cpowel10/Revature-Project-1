package com.revature.project1.services;

import com.revature.project1.dao.ItemDAO;
import com.revature.project1.model.Item;
import com.revature.project1.model.User;
import com.revature.project1.utilities.CheckNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    ItemDAO itemDAO;

    @Autowired()
    CheckNumber checkNumber;

    @Override
    public boolean addItem(Item item) {
        if(checkNumber.checkNegativeInt(item.getQoh(),item.getPrice())){
            itemDAO.save(item);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteItem(int itemId) {
        Item i = itemDAO.findById(itemId);
        if(i.getUser() != null){
            User u = i.getUser();
            u.getCartContents().remove(i);
        }
        itemDAO.deleteById(itemId);
        return true;
    }

    @Override
    public boolean updateItem(Item item) {
        if(checkNumber.checkNegativeInt(item.getQoh(),item.getPrice())){
            itemDAO.save(item);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String getAllInstockItems() {
        List<Item> instock = itemDAO.getAllInStock(Sort.by("itemId"));
        String str = "";
        for(Item i : instock){
            if(i.getUser() == null && i.getQoh() > 0){
                str = str + i.getItemId()+": "+i.getItemName()+"-- price: $"+i.getPrice() + "\n";
            }
            else{
                continue;
            }
        }

        return str;
    }

    @Override
    public boolean isItemExists(int itemId) {
        return itemDAO.existsById(itemId);
    }
}
