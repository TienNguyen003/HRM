package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Thông báo nội bộ. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_notification", indexes = {
        @Index(name = "idx_hrm_notif_unread", columnList = "unread")
})
public class HrmNotification {
    @Id
    @Column(length = 40)
    private String id;
    private String title;
    @Column(length = 1000)
    private String detail;
    private String date;
    @Column(name = "notif_group")
    private String group;
    private boolean unread;
    private String icon;
    private String tone;
    private String link;
}
