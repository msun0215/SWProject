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
	private String member_id;	// Email Type
	private String member_pw;
	private String member_name;
	private String member_nickname;
	private int member_role_id;

	// OAuth 제공자
	private String provider;
	private String providerId;
	//private Timestamp loginDate;
	@CreationTimestamp
	private Timestamp createDate;

	@Builder
	public Member(String member_id, String member_pw, String member_name, String member_nickname, int member_role_id, String provider, String providerId, Timestamp createDate) {
		this.member_id = member_id;
		this.member_pw = member_pw;
		this.member_name = member_name;
		this.member_nickname = member_nickname;
		this.member_role_id=member_role_id;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}
}