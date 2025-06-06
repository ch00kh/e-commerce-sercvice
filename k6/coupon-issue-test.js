import http from 'k6/http';
import { check, sleep } from 'k6';


export const options = {
    stages: [
        { duration: '10s', target: 100 },   // 10초 동안 100명까지 증가
        { duration: '10s', target: 100 },   // 10초 동안 100명까지 증가
        { duration: '20s', target: 500 },   // 15초 동안 500명까지 급증 (스파이크)
        { duration: '10s', target: 100 },    // 5초 동안 100명 유지
        { duration: '10s', target: 0 }       // 5초 동안 0명으로 감소
    ]

};

export default function () {

    const userId = __VU;

    const payload = JSON.stringify({
        userId: userId,
        couponId: 11
    });

    // 요청 헤더
    const params = {
        headers: {'Content-Type': 'application/json'},
        tags: { name: 'coupon_issue' },
        timeout: '60s'
    };

    const res = http.post('http://host.docker.internal:8080/api/v1/coupon/issue', payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(Math.random() * 2 + 1);
}
