package com.sparta.board.entity.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    NOT_VALID_TOKEN(400, "토큰이 유효하지 않습니다."),
    NOT_FOUND_USER(400, "사용자가 존재하지 않습니다."),
    NOT_FOUND_WRITING(400, "게시글/댓글이 존재하지 않습니다."),
    DUPLICATED_USERNAME(400, "중복된 ID입니다."),
    NOT_MATCH_INFO(400, "잘못된 ID/PW 입니다."),
    NOT_AUTHOR(400, "작성자만 삭제/수정할 수 있습니다.");

    private int statusCode;
    private String message;

}
