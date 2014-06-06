<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- 测试页面 -->
        <title>科研项目</title>
        <script type="text/javascript">  
            $(function() {

                var form1 = $('#form1').editableForm();
		
                form1.validate({submitHandler: function() { 
                        $('#form1')[0].submit();
                    }});
        
                $('.editButton').click(function (){
                    form1.editableForm('showEdit');
                    $('.editButton').hide();
                    $('.printButton').hide();
                    $('.cancelButton').show();
                    $('.saveButton').show();
		
                });
		
                $('.saveButton').click(function (){
                    $('#form1').submit();
                });
		
                $('.cancelButton').click(function (){
                    form1.editableForm('showView');
                    $('.cancelButton').hide();
                    $('.saveButton').hide();
                    $('.editButton').show();
                    $('.printButton').show();
                });
	
            });
        </script>
        <style type="text/css">
            .jbxx td {
                padding: 5px;
            }
            .jbxx td span {
                float: right;
            }

        </style>
    </head>
    <body>

        <div id="messages" style="color: red " ></div>

        <div class="formBtn" align="center">
            <input type="button" class="submitLongLong editButton" value="修改项目信息"  /> 
            <input type="button" class="submitLongLong printButton" value="项目信息打印"  /> 
            <input type="button" class="submitLong saveButton" value="保存" style="display: none" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="display: none" />
        </div>

        <form id="form1" action="${ctx}/demo/kyxmsave.do" method="post">

            <input type="hidden" name="bizobjs" value="xmxx:T_XM_JBXX"/>
            <input type="hidden" name="bizobjs" value="xmcy:T_XM_XMCY"/>
            <input type="hidden" name="xmxx.wid" value="${xmxx.wid}"/>

            <div class="tableGroup otherInfo">
                <h4 class="mRTableTitle" title="基本信息">
                    <i class="arrow"></i>基本信息
                </h4>
                <div>
                    <table style="width: 100%" class="jbxx">
                        <tr>
                             <td ><span>项目序号</span></td>
                            <td ><input name="xmxx.xmxh" class="{required:true}"   baseType="text"  value="${xmxx.xmxh}"/></td>
                            <td ><span>项目名称</span></td>
                            <td ><input name="xmxx.xmmc" class="{required:true}"   baseType="text"  value="${xmxx.xmmc}"/></td>
                            <td ><span>项目级别</span></td>
                            <td ><input name="xmxx.xmjb" class="{required:true}" baseType="xmjb" value="${xmxx.xmjb}"/></td>
                        <tr>   
                            <td ><span>项目一级类别</span></td>
                            <td ><input name="xmxx.xmlb" class="{required:true,minlength:2}" baseType="text" value="${xmxx.xmlb}"/></td>
                            <td><span>项目二级类别</span></td>
                            <td><input name="xmxx.xmejlb" class="{required:true}" baseType="text"  value="${xmxx.xmejlb}"/></td>
                            <td><span>学科</span></td>
                            <td><input name="xmxx.xk" baseType="text"  value="${xmxx.xk}"/></td>
                        </tr>
                        <tr>
                            <td><span>项目所属单位</span></td>
                            <td><input name="xmxx.xmssdw" class="{required:true}" baseType="text"  value="${xmxx.xmssdw}"/></td>
                            <td><span>开始日期</span></td>
                            <td><input name="xmxx.ksrq" baseType="date"  value="${xmxx.ksrq}"/></td>
                             <td><span>结束日期</span></td>
                            <td><input name="xmxx.jsrq" baseType="date"  value="${xmxx.jsrq}"/></td>
                        </tr>
                        <tr>
                            <td><span>合同经费</span></td>
                            <td><input  name="xmxx.htjf" baseType="text"  value="${xmxx.htjf}"/></td>
                            <td><span>备注</span></td>
                            <td><input  name="xmxx.bz" baseType="text"  value="${xmxx.bz}"/></td>
                        </tr>
                          <tr>
                            <td><span>项目简介</span></td>
                            <td><input  name="xmxx.xmjj" baseType="text"  value="${xmxx.xmjj}"/></td>
                        </tr>
                    </table>
                </div>

            </div>
               
            <div class="tableGroup otherInfo">
                <h4 class="mRTableTitle">
                    <i class="arrow"></i>项目成员
                </h4>
                <div>
                    <div class="formBtn">
                        <a href="" class="submitLongLong">添加成员</a>
                    </div>
                    <table class="table_con" width="100%" border="0"  cellpadding="0" cellspacing="0">
                        <tr class="tableTr">
                            <th width="15%">姓名</th>
                            <th width="15%">单位</th>
                            <th >职称</th>
                            <th width="20%">排名</th>
                            <th width="10%">操作</th>
                        </tr>
                        <c:forEach items="${xmcy}" var="xmcy">
                            <tr class="t_con">
                                <td><input name="xmcy.xm" class="{required:true}" value="${xmcy.xm}" baseType="text"/></td>
                                <td><input name="xmcy.dw" class="{required:true}" value="${xmcy.dw}" baseType="text"/></td>
                                <td><input name="xmcy.zc" class="{required:true}" value="${xmcy.zc}" baseType="text"/></td>
                                <td><input name="xmcy.pm" class="{required:true}" value="${xmcy.pm}" baseType="text"/></td>
                                <td >
                                    <div align="center">
                                        <a href="#">删除</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
                        
                        
        </form>
        <div class="formBtn" align="center">
            <input type="button" class="submitLongLong editButton" value="修改基本信息"  /> 
            <input type="button" class="submitLongLong printButton" value="基本信息打印"  /> 
            <input type="button" class="submitLong saveButton" value="保存" style="display: none" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="display: none" />
        </div>

    </body>
</html>