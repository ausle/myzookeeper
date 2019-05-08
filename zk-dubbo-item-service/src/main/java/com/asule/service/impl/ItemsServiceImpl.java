package com.asule.service.impl;

import com.asule.mapper.ItemsMapper;
import com.asule.entity.Items;
import com.asule.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("itemsServiceImpl")
public class ItemsServiceImpl implements ItemsService{

    @Autowired
    private ItemsMapper itemsMapper;

    @Override
    public Items getItem(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public int getItemCounts(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId).getCounts();
    }

    @Override
    public void displayReduceCounts(String itemId, int allCounts,int buyCounts) {
        Items reduceItem = new Items();
        reduceItem.setId(itemId);
        reduceItem.setCounts(allCounts);
        reduceItem.setBuyCounts(buyCounts);

        itemsMapper.reduceCounts(reduceItem);
    }
}
