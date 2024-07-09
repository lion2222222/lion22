package com.sparta.bitbucket.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sparta.bitbucket.auth.dto.SignupRequestDto;
import com.sparta.bitbucket.auth.dto.SignupResponseDto;
import com.sparta.bitbucket.auth.entity.Role;
import com.sparta.bitbucket.auth.entity.User;
import com.sparta.bitbucket.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	/**
	 * 회원가입 기능을 수행합니다.
	 *
	 * @param requestDto 회원가입 요청 정보를 담고 있는 DTO
	 * @return SignupResponseDto 회원가입 결과 정보를 담고 있는 DTO
	 * @throws IllegalArgumentException 이미 존재하는 이메일로 가입 시도할 경우 발생
	 */
	public SignupResponseDto signup(SignupRequestDto requestDto) {

		Optional<User> duplicateUser = userRepository.findByEmail(requestDto.getEmail());

		// 이메일 중복 확인
		if (duplicateUser.isPresent()) {
			throw new IllegalArgumentException("이메일 중복"); // todo : Exception 의논해서 만든 후 변경
		}

		User user = User.builder()
			.email(requestDto.getEmail())
			.name(requestDto.getName())
			.password(requestDto.getPassword())
			.role(Role.USER)
			.build();

		User savedUser = userRepository.save(user);

		return new SignupResponseDto(savedUser);
	}

}