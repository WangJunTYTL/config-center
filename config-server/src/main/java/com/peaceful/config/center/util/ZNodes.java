package com.peaceful.config.center.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peaceful.config.center.domain.Category;

/**
 * http://www.treejs.cn/v3/faq.php#_206
 *
 * 构造ZTree需要的数据结构
 *
 * Created by Jun on 2018/7/20.
 */
public class ZNodes {

    public static String buildZNodesJson(Category category) {
        if (category == null) {
            return null;
        }

        JSONArray root = new JSONArray();
        JSONObject node = new JSONObject();
        node.put("name", category.getName());
        node.put("open", true);
        node.put("id", category.getId());
        node.put("click", "queryCategoryProperties(" + category.getId() + ")");
        node.put("icon", "/plugin/zTree_v3/css/zTreeStyle/img/diy/1_open.png");
        render(category, node);
        root.add(node);
        return root.toJSONString();
    }

    private static void render(Category category, JSONObject node) {
        if (category.getChildrenCategoryList() != null && !category.getChildrenCategoryList().isEmpty()) {
            JSONArray list = new JSONArray();
            for (Category c : category.getChildrenCategoryList()) {
                JSONObject n = new JSONObject();
                n.put("name", c.getName());
                n.put("open", false);
                n.put("id", c.getId());
                n.put("pId", category.getId());
                n.put("click", "queryCategoryProperties(" + c.getId() + ")");
//                n.put("isParent","true");
                list.add(n);
                render(c, n);
            }
            node.put("children", list);
        }
    }
}
