<template>
  <router-view></router-view>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f7fa;
}

#app {
  height: 100%;
}

/* 全局过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* Element Plus 样式覆盖 */
.el-menu {
  border-right: none !important;
}

.el-menu-item.is-active {
  background-color: #409eff !important;
  color: #fff !important;
}

.el-menu-item:hover {
  background-color: #ecf5ff !important;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1) !important;
}

.el-form-item__label {
  font-weight: 500 !important;
}

.el-button {
  padding: 10px 20px !important;
  border-radius: 4px !important;
  font-weight: 500 !important;
}

.el-input__wrapper,
.el-select .el-input__wrapper {
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
}

.el-input__wrapper:hover,
.el-select .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #c0c4cc inset !important;
}

.el-input__wrapper.is-focus,
.el-select .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #409eff inset !important;
}
</style>
