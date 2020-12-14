package com.freelance.solutionhub.mma.model;

import java.util.Date;
import java.util.List;



public class NotificationReadModel{
    public List<Item> items;
    public int numberOfElements;
    public int pageNumber;
    public int pageSize;
    public int totalElements;
    public int totalPages;

    class Payload{
        public String event_publisher;
        public String mso_id;
        public String mso_type;
        public String status;
    }

    class Item{
        public String id;
        public Date inserted_at;
        public boolean is_read;
        public Payload payload;
        public String recipient_id;
        public String type;
        public Date updated_at;
    }
}
