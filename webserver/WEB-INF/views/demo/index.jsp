<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- 测试页面 -->
        <title>测试页面</title>

        <script type="text/javascript">  
            $(function() {

                var form1 = $('#form1').editableForm();
                
                $('#jbxx_mzdm').combobox({beforeselect : function (event, data){
                		alert(data.text);
            			alert(data.value);
                	    return true;
                	}});
                
                $('#jbxx_syd').combotree({afterselect : function (event, data){
            		alert(data.text);
        			alert(data.value);
            	    return true;
            	}});

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
                	$('#jbxx.csrq').date("setText","2001-01-01");
                    $('#form1').submit();
                });
		
                $('.cancelButton').click(function (){
                    form1.editableForm('showView');
                    $('.cancelButton').hide();
                    $('.saveButton').hide();
                    $('.editButton').show();
                    $('.printButton').show();
                });
                
                $('#dsjbxx').combotree({onlySelectLeaf : true});
	
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
            <input type="button" class="submitLongLong editButton" value="修改基本信息"  /> 
            <input type="button" class="submitLongLong printButton" value="基本信息打印"  /> 
            <input type="button" class="submitLong saveButton" value="保存" style="display: none" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="display: none" />
        </div>

        <form id="form1" action="${ctx}/demo/save.do" method="post">

            <input type="hidden" name="bizobjs" value="jbxx:T_XJGL_XJXX_YJSJBXX"/>
            <input type="hidden" name="bizobjs" value="jtcy:T_XJGL_XJXX_JTCYJBXX"/>

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
                            <td ><input name="jbxx.xm" class="{required:true,minlength:2}" baseType="text" value="${jbxx.xm}"/></td>
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
                            <td><input id="jbxx.csrq" name="jbxx.csrq" baseType="date"  value="${jbxx.csrq}"/></td>
                        </tr>
                        <tr>
                            <td><span>民族</span></td>
                            <td><input id="jbxx_mzdm" name="jbxx.mzdm"  baseType="mzdm"  value="${jbxx.mzdm}"/></td>
                            <td><span>政治面貌</span></td>
                            <td><input  name="jbxx.zzmmm" baseType="zzmm" value="${jbxx.zzmmm}"/></td>
                            <td><span>证件类型</span></td>
                            <td><input name="jbxx.zjlx" baseType="zjlx"  value="${jbxx.zjlx}"/></td>
                        </tr>
                        <tr>
                            <td><span>证件号码</span></td>
                            <td><input name="jbxx.zjhm" baseType="text"  value="${jbxx.zjhm}"/></td>
                            <td><span>婚姻状况</span></td>
                            <td><input name="jbxx.hyzkdm" baseType="hyzk"  value="${jbxx.hyzkdm}"  class="{required:true}"/></td>
                            <td><span>生育状况</span></td>
                            <td><input name="jbxx.syzk" baseType="text"  value="${jbxx.syzk}"  class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>国家地区</span></td>
                            <td><input name="jbxx.gjdqdm" baseType="gjdq"  value="${jbxx.gjdqdm}"/></td>
                            <td><span>籍贯</span></td>
                            <td><input name="jbxx.jg" baseType="xzqh"  value="${jbxx.jg}"/></td>
                            <td><span>出生地</span></td>
                            <td><input name="jbxx.csdm" baseType="xzqh"  value="${jbxx.csdm}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>生源地</span></td>
                            <td><input id="jbxx_syd" name="jbxx.syd" baseType="xzqh"  value="${jbxx.syd}"/></td>
                            <td><span>港澳台侨</span></td>
                            <td><input name="jbxx.gatqm" baseType="gat"  value="${jbxx.gatqm}"/></td>
                            <td><span>火车终点站</span></td>
                            <td><input name="jbxx.zdz" baseType="text"  value="${jbxx.zdz}"/></td>
                        </tr>
                        <tr>
                        <td><span>院系</span></td>
                            <td><input  name="jbxx.yxdm" baseType="yxdm"  value="${jbxx.yxdm}"/></td>
                            <td><span>专业</span></td>
                            <td><input  name="jbxx.zydm" baseType="zydm"  value="${jbxx.zydm}"/></td>
                             <td><span>导师</span></td>
                            <td><input name="jbxx.dszgh"  id="dsjbxx" baseType="dsjbxx" value="${jbxx.dszgh}"/></td>
                        </tr>
                        <tr>
                            <td><span>学生类别</span></td>
                            <td><input name="jbxx.xslbdm" baseType="xslb" value="${jbxx.xslbdm}"/></td>
                            <td><span>学历</span></td>
                            <td><input name="jbxx.xlm" baseType="xldm" value="${jbxx.xlm}"/></td>
                            <td><span>年级</span></td>
                            <td><input name="jbxx.nj" baseType="njdm" value="${jbxx.nj}"/></td>
                           
                        </tr>
                        <tr>
                            <td><span>培养方式</span></td>
                            <td><input name="jbxx.pyfsdm" baseType="pyfs" value="${jbxx.pyfsdm}"/></td>
                            <td><span>是否</span></td>
                            <td><input name="" baseType="yxdmTable" /></td>
                            <td><span>选择年月</span></td>
                            <td><input name="" baseType="monthPicker" separator="年" /></td>
                        </tr>
                    </table>
                </div>

            </div>

            <div class="tableGroup otherInfo">
                <h4 class="mRTableTitle">
                    <i class="arrow"></i>家庭情况
                </h4>
                <div>
                    <input type="hidden" name="jtcy.wid" value="${jtcy.wid}"/>
                    <input type="hidden" name="jtcy.xh" value="${jbxx.xh}"/>
                    <table style="width: 100%" class="jbxx">
                        <tr>
                            <td rowspan="2" width="6%"><span>父亲</span></td>
                            <td ><span>姓名</span></td>
                            <td ><input name="jtcy.fqxm" baseType="text" value="${jtcy.fqxm}" class="{required:true}"/></td>
                            <td ><span>年龄</span></td>
                            <td ><input name="jtcy.fqnl" baseType="text" value="${jtcy.fqnl}" class="{required:true}"/></td>
                            <td ><span>电话</span></td>
                            <td ><input name="jtcy.fqdh" baseType="text" value="${jtcy.fqdh}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>邮政编码</span></td>
                            <td><input name="jtcy.fqyzbm" baseType="text" value="${jtcy.fqyzbm}" class="{required:true}"/></td>
                            <td><span>工作单位</span></td>
                            <td colspan="5"><input name="jtcy.fqgzdw" baseType="text" value="${jtcy.fqgzdw}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td rowspan="2"><span>母亲</span></td>
                            <td><span>姓名</span></td>
                            <td><input name="jtcy.mqxm" baseType="text" value="${jtcy.mqxm}" class="{required:true}"/></td>
                            <td><span>年龄</span></td>
                            <td><input name="jtcy.mqnl" baseType="text" value="${jtcy.mqnl}" class="{required:true}"/></td>
                            <td><span>电话</span></td>
                            <td><input name="jtcy.mqdh" baseType="text" value="${jtcy.mqdh}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>邮政编码</span></td>
                            <td><input name="jtcy.mqyzbm" baseType="text" value="${jtcy.mqyzbm}" class="{required:true}"/></td>
                            <td><span>工作单位</span></td>
                            <td colspan="5"><input name="jtcy.mqgzdw" baseType="text" value="${jtcy.mqgzdw}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td rowspan="2"><span>配偶</span></td>
                            <td><span>姓名</span></td>
                            <td><input name="jtcy.poxm" baseType="text" value="${jtcy.poxm}"/></td>
                            <td><span>年龄</span></td>
                            <td><input name="jtcy.ponl" baseType="text" value="${jtcy.ponl}"/></td>
                            <td><span>电话</span></td>
                            <td><input name="jtcy.podh" baseType="text" value="${jtcy.podh}"/></td>
                        </tr>
                        <tr>
                            <td><span>邮政编码</span></td>
                            <td><input name="jtcy.poyzbm" baseType="text" value="${jtcy.poyzbm}"/></td>
                            <td><span>工作单位</span></td>
                            <td colspan="5"><input name="jtcy.pogzdw" baseType="text" value="${jtcy.pogzdw}"/></td>
                        </tr>
                    </table>
                </div>
            </div>


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
                            <td ><input name="jbxx.xm" class="{required:true,minlength:2}" baseType="text" value="${jbxx.xm}"/></td>
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
                            <td><input id="jbxx.csrq" name="jbxx.csrq" baseType="date"  value="${jbxx.csrq}"/></td>
                        </tr>
                        <tr>
                            <td><span>民族</span></td>
                            <td><input id="jbxx_mzdm" name="jbxx.mzdm"  baseType="mzdm"  value="${jbxx.mzdm}"/></td>
                            <td><span>政治面貌</span></td>
                            <td><input  name="jbxx.zzmmm" baseType="zzmm" value="${jbxx.zzmmm}"/></td>
                            <td><span>证件类型</span></td>
                            <td><input name="jbxx.zjlx" baseType="zjlx"  value="${jbxx.zjlx}"/></td>
                        </tr>
                        <tr>
                            <td><span>证件号码</span></td>
                            <td><input name="jbxx.zjhm" baseType="text"  value="${jbxx.zjhm}"/></td>
                            <td><span>婚姻状况</span></td>
                            <td><input name="jbxx.hyzkdm" baseType="hyzk"  value="${jbxx.hyzkdm}"  class="{required:true}"/></td>
                            <td><span>生育状况</span></td>
                            <td><input name="jbxx.syzk" baseType="text"  value="${jbxx.syzk}"  class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>国家地区</span></td>
                            <td><input name="jbxx.gjdqdm" baseType="gjdq"  value="${jbxx.gjdqdm}"/></td>
                            <td><span>籍贯</span></td>
                            <td><input name="jbxx.jg" baseType="xzqh"  value="${jbxx.jg}"/></td>
                            <td><span>出生地</span></td>
                            <td><input name="jbxx.csdm" baseType="xzqh"  value="${jbxx.csdm}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>生源地</span></td>
                            <td><input id="jbxx_syd" name="jbxx.syd" baseType="xzqh"  value="${jbxx.syd}"/></td>
                            <td><span>港澳台侨</span></td>
                            <td><input name="jbxx.gatqm" baseType="gat"  value="${jbxx.gatqm}"/></td>
                            <td><span>火车终点站</span></td>
                            <td><input name="jbxx.zdz" baseType="text"  value="${jbxx.zdz}"/></td>
                        </tr>
                        <tr>
                        <td><span>院系</span></td>
                            <td><input  name="jbxx.yxdm" baseType="yxdm"  value="${jbxx.yxdm}"/></td>
                            <td><span>专业</span></td>
                            <td><input  name="jbxx.zydm" baseType="zydm"  value="${jbxx.zydm}"/></td>
                             <td><span>导师</span></td>
                            <td><input name="jbxx.dszgh"  id="dsjbxx" baseType="dsjbxx" value="${jbxx.dszgh}"/></td>
                        </tr>
                        <tr>
                            <td><span>学生类别</span></td>
                            <td><input name="jbxx.xslbdm" baseType="xslb" value="${jbxx.xslbdm}"/></td>
                            <td><span>学历</span></td>
                            <td><input name="jbxx.xlm" baseType="xldm" value="${jbxx.xlm}"/></td>
                            <td><span>年级</span></td>
                            <td><input name="jbxx.nj" baseType="njdm" value="${jbxx.nj}"/></td>
                           
                        </tr>
                        <tr>
                            <td><span>培养方式</span></td>
                            <td><input name="jbxx.pyfsdm" baseType="pyfs" value="${jbxx.pyfsdm}"/></td>
                            <td><span>是否</span></td>
                            <td><input name="" baseType="yxdmTable" /></td>
                            <td><span>选择年月</span></td>
                            <td><input name="" baseType="monthPicker" separator="年" /></td>
                        </tr>
                    </table>
                </div>

            </div>
            
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
                            <td ><input name="jbxx.xm" class="{required:true,minlength:2}" baseType="text" value="${jbxx.xm}"/></td>
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
                            <td><input id="jbxx.csrq" name="jbxx.csrq" baseType="date"  value="${jbxx.csrq}"/></td>
                        </tr>
                        <tr>
                            <td><span>民族</span></td>
                            <td><input id="jbxx_mzdm" name="jbxx.mzdm"  baseType="mzdm"  value="${jbxx.mzdm}"/></td>
                            <td><span>政治面貌</span></td>
                            <td><input  name="jbxx.zzmmm" baseType="zzmm" value="${jbxx.zzmmm}"/></td>
                            <td><span>证件类型</span></td>
                            <td><input name="jbxx.zjlx" baseType="zjlx"  value="${jbxx.zjlx}"/></td>
                        </tr>
                        <tr>
                            <td><span>证件号码</span></td>
                            <td><input name="jbxx.zjhm" baseType="text"  value="${jbxx.zjhm}"/></td>
                            <td><span>婚姻状况</span></td>
                            <td><input name="jbxx.hyzkdm" baseType="hyzk"  value="${jbxx.hyzkdm}"  class="{required:true}"/></td>
                            <td><span>生育状况</span></td>
                            <td><input name="jbxx.syzk" baseType="text"  value="${jbxx.syzk}"  class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>国家地区</span></td>
                            <td><input name="jbxx.gjdqdm" baseType="gjdq"  value="${jbxx.gjdqdm}"/></td>
                            <td><span>籍贯</span></td>
                            <td><input name="jbxx.jg" baseType="xzqh"  value="${jbxx.jg}"/></td>
                            <td><span>出生地</span></td>
                            <td><input name="jbxx.csdm" baseType="xzqh"  value="${jbxx.csdm}" class="{required:true}"/></td>
                        </tr>
                        <tr>
                            <td><span>生源地</span></td>
                            <td><input id="jbxx_syd" name="jbxx.syd" baseType="xzqh"  value="${jbxx.syd}"/></td>
                            <td><span>港澳台侨</span></td>
                            <td><input name="jbxx.gatqm" baseType="gat"  value="${jbxx.gatqm}"/></td>
                            <td><span>火车终点站</span></td>
                            <td><input name="jbxx.zdz" baseType="text"  value="${jbxx.zdz}"/></td>
                        </tr>
                        <tr>
                        <td><span>院系</span></td>
                            <td><input  name="jbxx.yxdm" baseType="yxdm"  value="${jbxx.yxdm}"/></td>
                            <td><span>专业</span></td>
                            <td><input  name="jbxx.zydm" baseType="zydm"  value="${jbxx.zydm}"/></td>
                             <td><span>导师</span></td>
                            <td><input name="jbxx.dszgh"  id="dsjbxx" baseType="dsjbxx" value="${jbxx.dszgh}"/></td>
                        </tr>
                        <tr>
                            <td><span>学生类别</span></td>
                            <td><input name="jbxx.xslbdm" baseType="xslb" value="${jbxx.xslbdm}"/></td>
                            <td><span>学历</span></td>
                            <td><input name="jbxx.xlm" baseType="xldm" value="${jbxx.xlm}"/></td>
                            <td><span>年级</span></td>
                            <td><input name="jbxx.nj" baseType="njdm" value="${jbxx.nj}"/></td>
                           
                        </tr>
                        <tr>
                            <td><span>培养方式</span></td>
                            <td><input name="jbxx.pyfsdm" baseType="pyfs" value="${jbxx.pyfsdm}"/></td>
                            <td><span>是否</span></td>
                            <td><input name="" baseType="yxdmTable" /></td>
                            <td><span>选择年月</span></td>
                            <td><input name="" baseType="monthPicker" separator="年" /></td>
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