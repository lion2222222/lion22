import {getAccessToken} from './auth.js';
import {getRandomColor} from "./util.js";

export async function loadMainPageData() {
    try {
        const response = await fetch('http://localhost:8080/api/users', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getAccessToken()}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch main page data');
        }

        const result = await response.json();

        if (result.status === 200) {
            return result.data;  // 여기서 데이터를 반환합니다.
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('Error loading main page data:', error);
        throw error;  // 에러를 다시 던져서 호출한 곳에서 처리할 수 있게 합니다.
    }
}

export function renderBoards(boards) {
    const boardList = document.getElementById('boardList');
    boardList.innerHTML = '';

    const startElement = createBoardElement('+ New board', redirectToCreateBoard);
    boardList.appendChild(startElement);

    boards.forEach(board => {
        const boardElement = createBoardElement(board.title, () => openBoard(board.id));
        boardElement.style.backgroundColor = getRandomColor();
        boardList.appendChild(boardElement);
    });
}

function createBoardElement(text, onClick) {
    const element = document.createElement('div');
    element.className = 'board';
    element.textContent = text;
    element.onclick = onClick;
    return element;
}

export function openBoard(boardId) {
    console.log(`Opening board with id: ${boardId}`);
    // 추후 구현
}

export function redirectToCreateBoard() {
    window.location.href = '/create_board';
}

export async function createBoard(title, content) {
    try {
        const response = await fetch('http://localhost:8080/api/boards', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getAccessToken()}`
            },
            body: JSON.stringify({title, content})
        });

        console.log(response);
        if (!response.ok) {
            throw new Error('Failed to create board');
        }

        const result = await response.json();
        console.log(result);
        if (result.status === 201) {
            return result.data;  // 여기서 데이터를 반환합니다.
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('Error loading main page data:', error);
        throw error;  // 에러를 다시 던져서 호출한 곳에서 처리할 수 있게 합니다.
    }
}

export async function loadBoardData(boardId) {
    try {
        const response = await fetch(`http://localhost:8080/api/boards/${boardId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getAccessToken()}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch board page data');
        }

        const result = await response.json();

        if (result.status === 200) {
            return result.data;  // 여기서 데이터를 반환합니다.
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('Error loading board page data:', error);
        throw error;  // 에러를 다시 던져서 호출한 곳에서 처리할 수 있게 합니다.
    }
}

export function renderBoardMemberList(memberList) {

    let result = "";

    memberList.forEach(member => {
        result += " [ " + member.userName + " ] ";
    });

    return result;
}

export async function updateBoardData(title, content, memberList) {
    const boardTitleElement = document.getElementById('boardTitle');
    const boardContentElement = document.getElementById('boardContent');
    const boardMemberElement = document.getElementById('boardMember');
    if (boardTitleElement) {
        boardTitleElement.textContent = `${title}`;
    }
    if (boardContentElement) {
        boardContentElement.textContent = `${content}`;
    }
    if (boardMemberElement) {
        boardMemberElement.textContent = `${memberList}`;
    }
}

export async function editBoard(boardId, title, content) {
    fetch(`http://localhost:8080/api/boards/${boardId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${getAccessToken()}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: title,
            content: content
        })
    })
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('보드 수정 실패');
            }
            return response.json();
        })
        .then(data => {
            alert('보드 수정이 완료되었습니다.');
            window.location.href = '/board';  // 보드 페이지로 리다이렉트
        })
        .catch(error => {
            console.error('보드 수정 오류:', error);
            alert('보드 수정 중 오류가 발생했습니다: ' + error.message);
        });
}

export async function inviteBoard(boardId, email) {
    try {
        const response = await fetch(`http://localhost:8080/api/boards/${boardId}/invite`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email
            })
        });

        if (!response.ok) {
            throw new Error('보드 초대 실패');
        }

        const result = await response.json();

        if (result.status === 200) {
            let data = result.data;
            alert(`${data.boardTitle} 보드에 사용자${data.userName} 이 초대되었습니다.`);
            window.location.href = '/board';
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('보드 초대 오류:', error);
        alert('보드 초대 중 오류가 발생했습니다: ' + error.message);
    }
}