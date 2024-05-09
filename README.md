# 项目目录结构

## 前端

-   bbs-admin 为管理系统 web 端
    > 端口 8091
-   bbs-web 为论坛系统 web 端
    > 端口 8092

## 后端

-   ### bbsServer 为后端 springboot 项目文件夹
-   bbs-common 是管理系统和论坛系统共同依赖的模块
-   bbs-admin 为管理系统后端服务器
    > 端口 7071
-   bbs-web 为论坛系统后端服务器
    > 端口 7072

# 项目启动

## 前端

分别在 admin 和 web 目录下运行`npm install`,然后运行`npm run dev`即可

## 后端

直接用 idea 打开 bbsServer 文件夹，等待加载完依赖然后运行两个后端程序即可
