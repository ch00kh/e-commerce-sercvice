import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '1m', target: 1000 }, // 30초 동안 500명까지 증가
        { duration: '5m', target: 1000 }, // 5분 동안 500명 유지
        { duration: '1m', target: 0 },   // 30초 동안 0명까지 감소
    ]
};

export default function () {

    const res = http.get('http://host.docker.internal:8080/api/v1/products/best');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    // 응답 상태가 200이 아니면 errors 메트릭이 생성됩니다
    if (res.status !== 200) {
        console.error(`Error: Got status ${res.status}`);
    }

    sleep(Math.random() * 2 + 1); // 1-3초 대기
}

