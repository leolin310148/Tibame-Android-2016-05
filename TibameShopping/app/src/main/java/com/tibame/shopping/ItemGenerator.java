package com.tibame.shopping;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author leo
 */
public class ItemGenerator {

    static String[] images = {
            "DBAK3Z-A900783TN000_5746cc989bcfe.jpg",
            "DDAB3P-A9005DQAN000_574808696dae2.jpg",
            "DGBJA7-A90077GCW000_57480019e1bdd.jpg",
            "DGCC4C-A9006EWB8000_5746a9017a33c.jpg",
            "DHAV1Z-A90077KM8000_5746ced85f173.jpg",
            "DIAC2S-A9006T4YD000_56a5760268845.jpg",
            "DIBN3D-A90061KLV000_5747a2633729b.jpg",
            "DIBPJH-A9006GK1W000_5746a3133b455.jpg",
            "DMABBB-A74986631000_57482582b9237.jpg",
            "DMAC15-A9005SPQE000_5746c591f188d.jpg",
            "DMAI01-A9005XK3J000_5746c4ccc4c97.jpg",
            "DMAT07-A9006IDIM000_5747a4bf390ef.jpg",
            "DPADIK-A9006XDBU000_5746da5000754.jpg",
            "DPAI1P-A82289986000_5747f61ed0d22.jpg",
            "DMAF15-A83721319000_5746d758a8e0b"
    };

    public static void generateItems() {

        for (String image : images) {


            FirebaseAuth auth = FirebaseAuth.getInstance();

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemName", UUID.randomUUID().toString());
            itemMap.put("itemPrice", new Random().nextInt(5000) + 300);
            itemMap.put("url", "http://tibame.leolin.me/android/2016/05/sample_images/" + image);
            itemMap.put("userId", auth.getCurrentUser().getUid());

            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE, new Random().nextInt(80) - 80);
            itemMap.put("createdAt", instance.getTime().getTime());


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("items")
                    .push()
                    .setValue(itemMap);
        }

    }
}
