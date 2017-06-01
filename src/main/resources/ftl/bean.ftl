package ${package};

<#assign impo=""/>
<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
			<#if types["${key}"]["class"]??>
			<#if impo?index_of(";${types['${key}']['class']}") == -1>
import ${types["${key}"]["class"]};
			<#assign impo="${impo};${types['${key}']['class']}"/>
			</#if>
			</#if>
			</#if>
		</#list>
	</#items>
</#list>

/**
 * ${tableRemark}
 * @author ring-utils
 * @date ${.now}
 * @version V1.0
 */
public class ${clsName}  implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
	private ${types["${key}"].type} ${col.name};	//${col.REMARKS}
			</#if>
		</#list>
	</#items>
</#list>

<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
	public ${types["${key}"].type} get${col.name?cap_first}(){
		return ${col.name};
	}
	public void set${col.name?cap_first}(${types["${key}"].type} ${col.name}){
		this.${col.name}=${col.name};
	}
			</#if>
		</#list>
	</#items>
</#list>

}