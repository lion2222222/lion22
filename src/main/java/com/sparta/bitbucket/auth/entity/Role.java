package com.sparta.bitbucket.auth.entity;

import lombok.Getter;

/**
 * 사용자 역할을 정의하는 enum
 * <p>
 * USER 일반 사용자 역할
 * <p>
 * ADMIN 관리자 역할
 */
@Getter
public enum Role {
	USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

	private final String role;

	Role(String role) {
		this.role = role;
	}
}