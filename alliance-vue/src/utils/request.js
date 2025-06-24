import axios from "axios";
import { ElMessage } from 'element-plus';
import router from '../router';

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL || '', // 接口前缀
    timeout: 30000,
    withCredentials: true // 如果需要跨域带cookie
});

// 请求拦截器 - 自动带 token
request.interceptors.request.use(config => {
    if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    const userStr = localStorage.getItem("alliance-user");
    if (userStr) {
        const user = JSON.parse(userStr);
        if (user?.token) {
            config.headers['Authorization'] = 'Bearer ' + user.token;
        }
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 响应拦截器 - 统一处理状态码
request.interceptors.response.use(
    response => {
        let res = response.data;

        // 文件流直接返回
        if (response.config.responseType === 'blob') {
            return res;
        }

        // 如果返回的是字符串尝试转 JSON
        if (typeof res === 'string') {
            try {
                res = JSON.parse(res);
            } catch {}

        }

        // 如果返回code是401，跳转登录
        if (res.code === 401 || res.code === '401') {
            ElMessage.error(res.msg || '登录已失效，请重新登录');
            router.push("/login");
            return Promise.reject(new Error('未授权'));
        }

        return res;
    },
    error => {
        ElMessage.error('请求失败：' + error.message);
        return Promise.reject(error);
    }
);

export default request;