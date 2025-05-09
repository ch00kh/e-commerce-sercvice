import http from 'k6/http';
import { check } from 'k6';
import { sleep } from 'k6';

const count = 1000;

export const options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '10s', target: 0 }
    ],
};

export default function () {

    const res = http.get('http://host.docker.internal:8080/api/v1/products/best');
    // console.log(`응답 - 상태: ${res.status}, 본문: ${res.body}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    // 응답 상태가 200이 아니면 errors 메트릭이 생성됩니다
    if (res.status !== 200) {
        console.error(`Error: Got status ${res.status}`);
    }

    sleep(1);
}
