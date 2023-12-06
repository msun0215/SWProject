package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

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
	private Role roles;   // USER, MANAGER, ADMIN

	// OAuth 제공자
	private String provider;
	private String providerId;
	//private Timestamp loginDate;
	@CreationTimestamp
	private Timestamp createDate;

	//@Builder
//	public Member(String memberID, String memberPW, String memberName, String memberNickname, Role memberRoleId, String provider, String providerId, Timestamp createDate) {
//		this.memberID = memberID;
//		this.memberPW = memberPW;
//		this.memberName = memberName;
//		this.memberNickname = memberNickname;
//		this.roles=memberRoleId;
//		this.provider = provider;
//		this.providerId = providerId;
//		this.createDate = createDate;
//	}

	public boolean isSame(final Member member) {
		return memberID.equals(member.memberID);
	}
}