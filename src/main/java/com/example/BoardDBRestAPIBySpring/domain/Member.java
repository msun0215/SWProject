package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


//public class Member {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "member_id", updatable = false)
//	private Long id;
//
//	private String name;
//
//	@Builder
//	public Member(final String name) {
//		this.name = name;
//	}
//}
@Entity
@Data
@NoArgsConstructor
public class Member {
	@Id
	private String memberID;	// Email Type
	private String memberPW;
	private String memberName;
	private String memberNickname;

	@ManyToOne
	@JoinColumn(name = "ROLE_ID")
	private Role roles;   // USER, MANAGER, ADMIN

	// OAuth 제공자
	private String provider;
	private String providerId;
	//private Timestamp loginDate;
	@CreationTimestamp
	private Timestamp createDate;

	@Builder
	public Member(String memberID, String memberPW, String memberName, String memberNickname, Role memberRoleId, String provider, String providerId, Timestamp createDate) {
		this.memberID = memberID;
		this.memberPW = memberPW;
		this.memberName = memberName;
		this.memberNickname = memberNickname;
		this.roles=memberRoleId;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}
}