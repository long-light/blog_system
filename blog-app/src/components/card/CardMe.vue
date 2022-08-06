<template>
  <el-card>
    <img src="@/assets/img/logoV2.png" class="image"/>
    <h1 class="me-author-name">Longzx</h1>
    <div class="me-author-description">
      <span><i class="iconfont icon-xuexiao"></i> &nbsp;湖南大学 硕士  控制科学与工程</span>
      <br>
      <span><i class="iconfont icon-xuexiao"></i> &nbsp;中国矿业大学 本科  机器人工程</span>
      <br>
      <br>
      <span><i class="iconfont icon-gangwei"></i> &nbsp;深度模型优化算法工程师</span>
      <br>
      <span><i class="iconfont icon-gangwei"></i> &nbsp;服务端开发工程师</span>
    </div>
    <div class="me-author-tool">
      <i @click="showTool(qq)" :title="qq.title" class="iconfont icon-QQ-circle-fill"></i>
      <i @click="showTool(Wechat)" :title="Wechat.title" class="iconfont icon-weixin-copy"></i>
      <i @click="showTool(phone)" :title="phone.title" class="iconfont icon-dianhua-yuankuang"></i>
      <i @click="showTool(email)" :title="email.title" class="iconfont icon-youxiang"></i>
    </div>
  </el-card>

</template>

<script>
  export default {
    name: 'CardMe',
    data() {
      return {
        qq: {title: 'QQ', message: '2865030676'},
        Wechat: {
          title: 'WeChat',
          message: 'LZX201320162019'
        },
        phone: {title: 'Phone', message: '13013997609'},
        email: {title: 'email', message: 'longzx@hnu.edu.cn'},
      }
    },
    methods: {
      showTool(tool) {
        this.$message({
          duration: 0,
          showClose: true,
          dangerouslyUseHTMLString: true,
          message: '<strong>' + tool.message + '</strong>'
        });
      }
    }
  }
</script>

<style scoped>
  .me-author-name {
    text-align: center;
    font-size: 30px;
    font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;
    border-bottom: 1px solid #65C2EA;
  }

  .me-author-description {
    padding: 8px 0;
  }

  .me-icon-job {
    padding-left: 16px;
  }

  .me-author-tool {
    text-align: center;
    padding-top: 10px;
  }

  .me-author-tool i {
    cursor: pointer;
    padding: 4px 10px;
    font-size: 30px;
  }
  .image {
      width: 100%;
      display: block;
    }
</style>
