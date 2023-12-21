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
	//@Enumerated(EnumType.STRING)
	private Role roles;   // USER, MANAGER, ADMIN

	// OAuth 제공자
	private String provider;
	private String providerId;
	//private Timestamp loginDate;
	@CreationTimestamp
	private Timestamp createDate;

	public boolean isSame(final Member member) {
		return memberID.equals(member.memberID);
	}

	public String getRoleName() {
		return roles.getRoleName();
	}

	public boolean isSameRole(final String roleName) {
		return roles.isSame(roleName);
	}

	private boolean isAdmin() {
		return roles.isSame("ADMIN");
	}

	public boolean isNotOwnerFor(final Ownable ownable) {
		return !ownable.isOwner(this);
	}

	public boolean hasNotDeletePermissionFor(final Ownable ownable) {
		if (isAdmin()) {
			return false;
		}

		return !ownable.isOwner(this);
    }
}