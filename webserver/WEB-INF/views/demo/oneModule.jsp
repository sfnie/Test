<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- 测试页面 -->
        <title>oneModule</title>
        <script type="text/javascript">  
            $(function() {
                var form1 = $('#form1').editableForm({autoValidation : true});
                
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
                
                $('#attachment').attachment({remove :  function (e,id){
                	  if(confirm('确定要删除' + id + "吗?")){
                    	  return true;

                	  }else{
                		  return false;
                	  }
                }});
                
               
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
        <button onclick="window.location.href='../navmenu.do?menuItemWid=A987CD69664E4213860F8C2D042B6814'">测试</button>
        <div class="formBtn" align="center">
            <input type="button" class="submitLongLong editButton" value="修改基本信息"  /> 
            <input type="button" class="submitLongLong printButton" value="基本信息打印"  /> 
            <input type="button" class="submitLong saveButton" value="保存" style="display: none" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="display: none" />
        </div>

        <form id="form1" action="${ctx}/demo/oneModuleSave.do" method="post">

            <input type="hidden" name="bizobjs" value="jbxx:T_XJGL_XJXX_YJSJBXX"/>
            <input type="hidden" name="jbxx.wid" value="${jbxx.wid}"/>

            <div class="tableGroup otherInfo">
                <h4 class="mRTableTitle" title="基本信息">
                    <i class="arrow"></i>基本信息
                </h4>
                <div>
                    <table style="width: 100%" class="jbxx">
                        <tr>
                            <td ><span>学号</span></td>
                            <td ><input name="jbxx.xh" baseType="text" readonly="true" value="${jbxx.xh}"/></td>
                            <td ><span>姓名</span></td>
                            <td ><input name="jbxx.xm" class="{required:true}" baseType="text" value="${jbxx.xm}"/></td>
                            <td ><span>姓名拼音</span></td>
                            <td ><input name="jbxx.xmpy" class="{required:true,minlength:3}" baseType="text" value="${jbxx.xmpy}"/></td>
                            <td rowspan="6">
                                <span style="float:none;"><input name="jbxx.zp" baseType="photo" bh="${jbxx.xh}" value="${jbxx.zp}"/></span>
                            </td>
                        </tr>
                        <tr>
                            <td><span>曾用名</span></td>
                            <td><input name="jbxx.cym" baseType="text" value="${jbxx.cym}"/></td>
                            <td><span>性别</span></td>
                            <td><input name="jbxx.xbdm" class="{required:true}" baseType="xbdm"  value="${jbxx.xbdm}"/></td>
                            <td><span>出生日期</span></td>
                            <td><input name="jbxx.csrq" baseType="date"  value="2009-06-21 13:11"/></td>
                        </tr>
                        <tr>
                            <td><span>民族</span></td>
                            <td><input name="jbxx.mzdm"  baseType="mzdm"  value="${jbxx.mzdm}"/></td>
                            <td><span>政治面貌</span></td>
                            <td><input  name="jbxx.zzmmm" baseType="zzmm" value="${jbxx.zzmmm}"/></td>
                            <td><span>证件类型</span></td>
                            <td><input name="jbxx.zjlx" baseType="zjlx"  value="${jbxx.zjlx}"/></td>
                        </tr>
                        <tr>
                            <td><span>证件号码</span></td>
                            <td><input name="jbxx.zjhm" baseType="text"  value="${jbxx.zjhm}"/></td>
                            <td><span>婚姻状况</span></td>
                            <td><input name="jbxx.hyzkdm" baseType="hyzk"  value="${jbxx.hyzkdm}"/></td>
                            <td><span>生育状况</span></td>
                            <td><input name="jbxx.syzk" baseType="text"  value="${jbxx.syzk}"/></td>
                        </tr>
                        <tr>
                            <td><span>国家地区</span></td>
                            <td><input name="jbxx.gjdqdm" baseType="gjdq"  value="${jbxx.gjdqdm}"/></td>
                            <td><span>籍贯</span></td>
                            <td><input name="jbxx.jg" baseType="xzqh"  value="${jbxx.jg}"/></td>
                            <td><span>出生地</span></td>
                            <td><input name="jbxx.csdm" baseType="xzqh"  value="${jbxx.csdm}"/></td>
                        </tr>
                        <tr>
                            <td><span>生源地</span></td>
                            <td><input  name="jbxx.syd" baseType="xzqh"  value="${jbxx.syd}"/></td>
                            <td><span>港澳台侨</span></td>
                            <td><input name="jbxx.gatqm" baseType="gat"  value="${jbxx.gatqm}"/></td>
                            <td><span>火车终点站</span></td>
                            <td><input name="jbxx.zdz" baseType="text"  value="${jbxx.zdz}"/></td>
                        </tr>
                        <tr>
                            <td><span>附件</span></td>
                            <td colspan="5">
                              <input serviceId="${jbxx.wid}" baseType="attachment" id="attachment" name="jbxx.fj"/>
                            </td>
                        </tr>
                        
                        <tr>
                            <td><span>备注</span></td>
                            <td colspan="5">
                              <textarea id="bz" height="100" width="600" name="jbxx.bz" baseType="textarea" class="{required : true}">${jbxx.bz}</textarea>
                            </td>
                        </tr>
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