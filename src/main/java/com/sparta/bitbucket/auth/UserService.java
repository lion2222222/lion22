package com.sparta.bitbucket.auth;

import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sparta.bitbucket.auth.dto.SignupRequestDto;
import com.sparta.bitbucket.auth.dto.SignupResponseDto;
import com.sparta.bitbucket.auth.dto.UserBoardsResponseDto;
import com.sparta.bitbucket.auth.entity.Role;
import com.sparta.bitbucket.auth.entity.User;
import com.sparta.bitbucket.board.BoardMemberRepository;
import com.sparta.bitbucket.board.entity.Board;
import com.sparta.bitbucket.board.entity.BoardMember;
import com.sparta.bitbucket.common.entity.ErrorMessage;
import com.sparta.bitbucket.common.exception.auth.UsernameDuplicateException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BoardMemberRepository boardMemberRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${manager-secret-key}")
	private String secretKey;

	/**
	 * 회원가입 기능을 수행합니다.
	 *
	 * @param requestDto 회원가입 요청 정보를 담고 있는 DTO
	 * @return SignupResponseDto 회원가입 결과 정보를 담고 있는 DTO
	 * @throws UsernameDuplicateException 이미 존재하는 이메일로 가입 시도할 경우 발생
	 */
	public SignupResponseDto signup(SignupRequestDto requestDto) {

		Optional<User> duplicateUser = userRepository.findByEmail(requestDto.getEmail());

		// 이메일 중복 확인
		if (duplicateUser.isPresent()) {
			throw new UsernameDuplicateException(ErrorMessage.USER_EMAIL_DUPLICATE);
		}

		Role role = setUserRole(requestDto.getSecretKey());

		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		User user = User.builder()
			.email(requestDto.getEmail())
			.name(requestDto.getName())
			.password(encodedPassword)
			.role(role)
			.build();

		User savedUser = userRepository.save(user);

		return new SignupResponseDto(savedUser);
	}

	public UserBoardsResponseDto getUserBoards(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_EMAIL_NOT_FOUND.getMessage()));

		List<Board> boards = boardMemberRepository.findByUser(user).stream().map(BoardMember::getBoard).toList();

		return UserBoardsResponseDto.builder().username(user.getName()).boards(boards).build();

	}

	private Role setUserRole(String providedSecretKey) {
		if (providedSecretKey != null && !providedSecretKey.isBlank()) {
			if (MessageDigest.isEqual(secretKey.getBytes(), providedSecretKey.getBytes())) {
				return Role.MANAGER;
			}
		}
		return Role.USER;
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_EMAIL_NOT_FOUND.getMessage()));
	}

}
