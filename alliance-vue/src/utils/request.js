import { ElMessage } from 'element-plus'
import router from '../router'
import axios from "axios";

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000,
    withCredentials: true
})

// 请求拦截器 - 添加 token
request.interceptors.request.use(config => {
  // 判断是否为 FormData
  const isFormData = config.data instanceof FormData;

  // 设置默认 Content-Type（非 FormData 且未显式指定）
  if (!isFormData && !config.headers['Content-Type']) {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
  }

  // 从 localStorage 中获取用户 token，添加 Authorization 头
  const userStr = localStorage.getItem("alliance-user");
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      if (user?.token) {
        config.headers['Authorization'] = 'Bearer ' + user.token;
      } else {
        console.warn("⚠️ 没有找到 token");
      }
    } catch (e) {
      console.warn("⚠️ 用户信息解析失败", e);
    }
  } else {
    console.warn("⚠️ localStorage 中没有登录信息");
  }

  return config;
}, error => {
  return Promise.reject(error);
});


// 响应拦截器 - 统一处理响应
request.interceptors.response.use(
    response => {
        let res = response.data;

        // 文件流直接返回
        if (response.config.responseType === 'blob') {
            return res;
        }

        if (typeof res === 'string') {
            try {
                res = JSON.parse(res);
            } catch (e) {
                // 不是 JSON 字符串，保持原样
            }
        }

        // 兼容数字或字符串的 code 判断
        if (res.code === 401 || res.code === '401') {
            ElMessage.error(res.msg || '登录已失效，请重新登录');
            router.push("/login");
            return Promise.reject(new Error('未授权'));
        }

        return res;
    },
    error => {
        console.error('请求失败:', error);
        ElMessage.error('请求失败：' + error.message);
        return Promise.reject(error);
    }
);

export default request;
