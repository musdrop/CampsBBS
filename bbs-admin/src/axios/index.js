import axios from "axios";
import { ElMessage } from "element-plus";
import global from "../global";
import router from "../router";

// 创建axios实例
const instance = axios.create({
	baseURL: global.baseURL,
	timeout: 5000, // 请求超时时间
});

//添加请求拦截器
instance.interceptors.request.use(
	function (config) {
		return config;
	},
	function (error) {
		return Promise.reject(error);
	}
);
//添加响应拦截器
instance.interceptors.response.use(
	function (response) {
		//sessios过期，要求重新登录，后端返回状态码为999
		if (response.data.code === 999) {

			//处理session过期重复弹窗问题
			//1.看看session中有没有msgFlag
			let megFlag = sessionStorage.getItem("msgFlag");
			//2.如果没有
			if (!megFlag) {
				ElMessage.error(response.data.msg);
				//在session中添加msgFlag属性并设为真
				sessionStorage.setItem("msgFlag", true);
				//3秒后删除msgFlag
				setTimeout(() => {
					sessionStorage.removeItem("msgFlag");
				}, 3000);
			}
			//3，跳转到登录页
			router.push("/login");
			//不再往后续传递响应
			return false;
        }
        //请求失败，返回值一般为-1，说明请求在后端出现了一些问题，response.data.msg中是后端传回的消息
        if (response.data.code !== 0) {
            console.log("接口请求失败")
            console.log(response)
			//弹出错误消息
            ElMessage.error(response.data.msg);
			//不再往后续传递响应
            return false
        }
		//如果返回的数据中有data字段，说明请求数据成功，返回data字段
        if(response.data.data){
            return response.data.data
        }
		//如果没有data字段，说明单纯请求成功，返回整个response.data
        return true
	},
	function (error) {
		return Promise.reject(error);
	}
);

export default instance;