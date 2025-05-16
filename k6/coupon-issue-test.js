import http from 'k6/http';
import { sleep } from 'k6';

const couponId = 14;

export const options = {
    stages: [
        { duration: '5s', target: 50 },
        { duration: '20s', target: 50 },
        { duration: '5s', target: 0 }
    ]
};

export default function () {

    const userId = __VU;

    const payload = JSON.stringify({
        userId: userId,
        couponId: couponId
    });

    // 요청 헤더
    const params = {
        headers: {'Content-Type': 'application/json'},
        tags: { name: 'coupon_issue' },
        timeout: '60s'
    };

    // 거의 동시에 요청하기 위해 약간의 지연만 추가
    sleep(Math.random() * 0.1);

    const res = http.post('http://host.docker.internal:8080/api/v1/coupon/issue', payload, params);

    console.log(`사용자 ${userId} 응답 - 상태: ${res.status}, 본문: ${res.body}`);
}
