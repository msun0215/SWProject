package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
//@NoArgsConstructor
public class Member {
	@Id
	private String memberID;	// Email Type
	private String memberPW;
	private String memberName;
	private String memberNickname;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RoleID")
	//@Enumerated(EnumType.STRING)
	private Role roles;   // USER, MANAGER, ADMIN

	// OAuth 제공자
	private String provider;
	private String providerId;
	//private Timestamp loginDate;
	@CreationTimestamp
	private Timestamp createDate;


}