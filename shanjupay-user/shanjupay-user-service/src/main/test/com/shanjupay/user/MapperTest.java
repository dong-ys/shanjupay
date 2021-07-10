package com.shanjupay.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.AuthorizationService;
import com.shanjupay.user.api.ResourceService;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeTreeDTO;
import com.shanjupay.user.api.dto.authorization.RoleDTO;
import com.shanjupay.user.api.dto.menu.MenuDTO;
import com.shanjupay.user.api.dto.menu.MenuQueryDTO;
import com.shanjupay.user.api.dto.resource.ApplicationDTO;
import com.shanjupay.user.api.dto.resource.ApplicationQueryParams;
import com.shanjupay.user.api.dto.resource.ResourceAPPDTO;
import com.shanjupay.user.api.dto.resource.ResourceDTO;
import com.shanjupay.user.api.dto.tenant.AbilityDTO;
import com.shanjupay.user.api.dto.tenant.TenRolePrivilegeDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import com.shanjupay.user.convert.AuthorizationPrivilegeConvert;
import com.shanjupay.user.convert.AuthorizationRoleConvert;
import com.shanjupay.user.convert.ResourceMenuConvert;
import com.shanjupay.user.entity.*;
import com.shanjupay.user.mapper.*;
import com.shanjupay.user.service.AuthorizationServiceImpl;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {

    @Resource
    private AuthorizationRoleMapper authorizationRoleMapper;
    @Resource
    private AccountRoleMapper accountRoleMapper;
    @Resource
    private AuthorizationPrivilegeMapper authorizationPrivilegeMapper;
    @Resource
    private ResourceApplicationMapper applicationMapper;
    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private ResourceMenuMapper menuMapper;
    @Autowired
    private AuthorizationPrivilegeGroupMapper groupMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AuthorizationRolePrivilegeMapper rolePrivilegeMapper;
    @Reference
    private ResourceService resourceService;
    @Reference
    private AuthorizationService authService;



    //获取某用户在多个租户下的权限信息
    @Test
    public void testAuthorize(){
        //入参：tring username, Long[] tenantIds
        //返回值：Map<Long, AuthorizationInfoDTO>
        String username = "员工A";
        Long[] tenantIds = {1151761810982985002L, 1151761810982985003L};
        Map<Long, AuthorizationInfoDTO> authorize = authService.authorize(username,tenantIds);
        System.out.println("===============map==============="+authorize);

     /*打印结果：{1151761810982985002=AuthorizationInfoDTO(rolePrivilegeMap={r_002=[p_002, p_003]}),
                 1151761810982985003=AuthorizationInfoDTO(rolePrivilegeMap={r_005=[p_005, p_004, p_007, p_006]})}*/


    }
    //用户在多个租户下的资源，按照应用划分
    @Test
    public void testLoadResources2(){
        String username = "员工A";
        Long[] tenantIds = {1151761810982985002L, 1151761810982985003L};
        Map<Long, AuthorizationInfoDTO> authorize = authService.authorize(username,tenantIds);
        System.out.println(authorize);
        Map<Long, List<ResourceDTO>> longListMap = resourceService.loadResources(authorize);
    }

    //根据权限加载指定应用的资源
    @Test
    public void testLoadResources(){
       // List<String> privilegeCodes, String applicationCode
        List<String> p = new ArrayList<>();
        p.add(0,"p_002");
        p.add(1,"p_003");
        String appCode = "a_001";
        List<ResourceDTO> resources = resourceService.loadResources(p, appCode);
        System.out.println(resources);

    }


    @Test
    public void test19(){
        Account account = new Account();
        account.setUsername("haha2333");
        account.setId(1171761810982985007L);
        accountMapper.update(null,new UpdateWrapper<Account>().lambda().eq(Account::getId,account.getId()).set(Account::getUsername,account.getUsername()));

    }

    @Test
    public void test18(){
        List<Long> pids = new ArrayList<>();
        pids.add(0,1001L);
        pids.add(1,1002L);
        rolePrivilegeMapper.insertRolePrivilege(1111L,pids);
    }

    @Test
    public void test17(){
        String aa="[{\"name\":\"角色二\",\"code\": \"r_012\",\"privilegeCodes\": [{\"privilegeCode\": \"p_002\"},{\"privilegeCode\":\"p_003\"}]},{\"name\": \"角色十三\",\"code\": \"r_013\",\"privilegeCodes\": [{\"privilegeCode\": \"p_005\"},{\"privilegeCode\": \"p_006\"}]}]";
        List<RoleDTO> objects = JSONObject.parseArray(aa,RoleDTO.class);

        List<AuthorizationRole> roles = AuthorizationRoleConvert.INSTANCE.dtolist2entity(objects);
        //1.租户下新增角色
        Long tenantId = 1151761810982985006L;
        authorizationRoleMapper.createRoles(tenantId,roles);

       // @SelectKey(before=false,keyProperty="obj.id",statementType=StatementType.STATEMENT,statement="SELECT LAST_INSERT_ID() AS id", resultType =short.class)
    }

    @Test
    public void test16(){
      String aa="[{\"roleName\":\"角色二\",\"roleCode\": \"r_012\",\"privileges\": [{\"privilegeCode\": \"p_002\",\"privilegeGroupId\": \"1221761810982985001\"},{\"privilegeCode\":\"p_003\",\"privilegeGroupId\":\"1221761810982985001\"}]},{\"roleName\": \"角色三\",\"roleCode\": \"r_003\",\"privileges\": [{\"privilegeCode\": \"p_005\",\"privilegeGroupId\": \"1221761810982985002\"},{\"privilegeCode\": \"p_006\",\"privilegeGroupId\": \"1221761810982985002\"}]}]";
        //JSONArray jsonArray = JSONObject.parseArray(aa);
        List<AbilityDTO> objects = JSONObject.parseArray(aa,AbilityDTO.class);

        System.out.println(objects);
        //2、循环遍历这个数组
        //for(int i=0;i<jsonArray.size();i++){
        //    //3、把里面的对象转化为JSONObject
        //    JSONObject job = jsonArray.getJSONObject(i);
        //    // 4、把里面想要的参数一个个用.属性名的方式获取到
        //    System.out.println(job.get("roleName")+"------"+job.get("roleCode")+"------"+job.get("privileges")) ;
        //}
        //角色二------r_002------[{"privilegeCode":"p_002","privilegeGroupId":"1221761810982985001"},{"privilegeCode":"p_003","privilegeGroupId":"1221761810982985001"}]
        //角色三------r_003------[{"privilegeCode":"p_005","privilegeGroupId":"1221761810982985002"},{"privilegeCode":"p_006","privilegeGroupId":"1221761810982985002"}]


    }

    @Test
    public void test15(){
        Account a = new Account();
        a.setId(1171761810982985001L);
        a.setUsername("哈哈");
        accountMapper.updateById(a);
    }

    @Test
    public void test14(){
        Account a = new Account();
        a.setId(1177111672632168450L);
        a.setUsername("sh-1");
        Account account = accountMapper.selectById(a.getId());
        System.out.println("=================="+account);

    }

    @Test
    public void test13(){
        String[] roleCodes = {"r_002","r_003","r_004","r_006","r_007"};
        List<String> roleList = new ArrayList<>(Arrays.asList(roleCodes));
        //roleList.add("r_007");
        //先获取某租户下的角色
        List<String> codes= Arrays.asList(roleCodes);
        List<AuthorizationRole> roles = authorizationRoleMapper.selectList(new QueryWrapper<AuthorizationRole>().lambda()
                .eq(AuthorizationRole::getTenantId,1151761810982985002L)
                .in(AuthorizationRole::getCode,codes));
        //获取多个角色的权限权限集合
        List<AuthorizationPrivilege> privileges = new ArrayList<>();
        if(!roles.isEmpty()){
            List<Long> roleIds = roles.stream().map(AuthorizationRole::getId).collect(Collectors.toList());
            privileges = authorizationPrivilegeMapper.selectPrivilegeByRole(roleIds);
        }
        List<PrivilegeDTO> pList = AuthorizationPrivilegeConvert.INSTANCE.entitylist2dto(privileges);
        System.out.println("################111111#############"+pList);
        HashSet<PrivilegeDTO> h = new HashSet<PrivilegeDTO>(pList);
        pList.clear();
        pList.addAll(h);

        System.out.println("################112222221#############"+pList);

        //pList = new ArrayList<>(h);
        //System.out.println("################22222#############"+pList);

        //1.获取租户下角色对应的权限集合
        //List<PrivilegeDTO> pList = queryPrivilege(tenantId, roleCodes);
        //2.获取所有权限组
        List<AuthorizationPrivilegeGroup> groupList = groupMapper.selectList(null);
        System.out.println("55555555555555555"+groupList);
        Map<String, PrivilegeTreeDTO> groupsMap = new HashMap<>();
        String topId = "top_01";
        PrivilegeTreeDTO topTree = new PrivilegeTreeDTO();
        topTree.setId(topId);
        topTree.setParentId(null);
        topTree.setName(null);
        topTree.setStatus(0);

        for (AuthorizationPrivilegeGroup g : groupList) {//所有权限组
            if(g.getParentId() == null) {//权限组的父id为空，则为顶级节点
                PrivilegeTreeDTO child = new PrivilegeTreeDTO();
                child.setId(String.valueOf(g.getId()));
                child.setParentId(topId);
                child.setName(g.getName());
                child.setGroup(true);
                child.setStatus(1);
                topTree.getChildren().add(child);//构造权限树，并添加子节点
                privGroupTree(child, groupList, groupsMap);//筛选
            }
        }

        for (PrivilegeDTO priv : pList) {//所有权限

            String privGroupId = String.valueOf(priv.getPrivilegeGroupId());
            PrivilegeTreeDTO pGroupTreeDto = groupsMap.get(privGroupId);//权限树
            if (pGroupTreeDto != null) {//不为空，构造权限树
                PrivilegeTreeDTO pTreeDto = new PrivilegeTreeDTO();
                pTreeDto.setGroup(false);
                pTreeDto.setName(priv.getName());
                pTreeDto.setId(priv.getCode());
                pTreeDto.setParentId(privGroupId);
                pTreeDto.setStatus(1);

                pGroupTreeDto.getChildren().add(pTreeDto);
            }
        }
        //return topTree;
        JSON.toJSON(topTree);
        System.out.println(JSON.toJSON(topTree));
        System.out.println(topTree);
    }

    //参数：
    public void privGroupTree(PrivilegeTreeDTO currChild, List<AuthorizationPrivilegeGroup> groupList, Map<String, PrivilegeTreeDTO> groupsMap) {
        if (!groupsMap.containsKey(currChild.getId())) {//权限树不包含叶子，则添加
            groupsMap.put(currChild.getId(), currChild);
        }
        for (AuthorizationPrivilegeGroup ccGroup : groupList) {//遍历权限组
            if (String.valueOf(ccGroup.getParentId()).equals(currChild.getId())) {

                PrivilegeTreeDTO tmp = new PrivilegeTreeDTO();
                tmp.setId(String.valueOf(ccGroup.getId()));
                tmp.setParentId(currChild.getId());
                tmp.setName(ccGroup.getName());
                tmp.setGroup(true);
                tmp.setStatus(1);
                currChild.getChildren().add(tmp);

                if (!groupsMap.containsKey(tmp.getId())) {
                    groupsMap.put(tmp.getId(), tmp);
                }

                privGroupTree(tmp, groupList, groupsMap);

            }
        }
    }

    @Test
    public void test12(){

        List<String> pcodes1 = new ArrayList<>();
        pcodes1.add(0,"p_002");
        pcodes1.add(1,"p_003");
        List<String> pcodes2 = new ArrayList<>();
        pcodes2.add(0,"p_005");
        pcodes2.add(1,"p_006");

        Map<String,List<String>>  rolePrivilegeMap1 = new HashMap<>();
        rolePrivilegeMap1.put("r_002",pcodes1);
        Map<String,List<String>>  rolePrivilegeMap2 = new HashMap<>();
        rolePrivilegeMap2.put("r_005",pcodes2);

        AuthorizationInfoDTO dto1 = new AuthorizationInfoDTO();
        dto1.setRolePrivilegeMap(rolePrivilegeMap1);

        AuthorizationInfoDTO dto2 = new AuthorizationInfoDTO();
        dto2.setRolePrivilegeMap(rolePrivilegeMap2);

        Map<Long, AuthorizationInfoDTO> map = new HashMap<>();
        map.put(1151761810982985002L,dto1);
        map.put(1151761810982985003L,dto2);
        System.out.println(map);
        /*if(map.isEmpty()) {
            throw new BusinessException(CommonErrorCode.E_200202);
        }*/
        Map<Long, List<ResourceDTO>> resultResource = new HashMap<>();
        Map<String, List<ResourceDTO>> tmpMap = new HashMap<>();

        //获取多个租户的权限
        for (Map.Entry<Long, AuthorizationInfoDTO> entry : map.entrySet()) {
            AuthorizationInfoDTO dto = entry.getValue();//结构：rolecode1 [pcode1,pcode2]
            Map<String, List<String>> rolePrivilegeMap = dto.getRolePrivilegeMap();
            Set<String> privCodeSet = new HashSet<>();
            for (Map.Entry<String, List<String>> entry2 : rolePrivilegeMap.entrySet()) {
                List<String> pcodelist = entry2.getValue();//租户某个角色下的权限
                //根据权限获取资源（此处为资源的一种类型：menu）
                privCodeSet.addAll(pcodelist);
            }
            List<String> aa = new ArrayList<>(privCodeSet);
            //查询到权限对应的资源和应用
            List<ResourceAPPDTO> result = menuMapper.selectResource(aa);
            List<ResourceDTO> r = new ArrayList<>();
            for (ResourceAPPDTO resApp : result) {
                //if(tmpMap.containsKey(entry.getKey() + "" + resApp.getApplicationCode())) {
                    ResourceDTO resourceDTO = new ResourceDTO();
                    resourceDTO.setApplicationCode(resApp.getApplicationCode());
                    resourceDTO.setApplicationName(resApp.getName());

                    ResourceMenu m = new ResourceMenu();
                    m.setParentId(resApp.getParentId());
                    m.setTitle(resApp.getTitle());
                    m.setUrl(resApp.getUrl());
                    m.setIcon(resApp.getIcon());
                    m.setSort(resApp.getSort());
                    m.setComment(resApp.getComment());
                    m.setStatus(resApp.getStatus());
                    m.setApplicationCode(resApp.getApplicationCode());
                    m.setPrivilegeCode(resApp.getPrivilegeCode());
                    List<ResourceMenu> obj = new ArrayList<>();
                    obj.add(m);
                    resourceDTO.getAppRes().put("menu",obj);
                    r.add(resourceDTO);
               // }
            }

            resultResource.put(entry.getKey(),r);

        }
    }

    @Test
    public void test11(){
        List<String> pcodes= new ArrayList<>();
        pcodes.add(0,"p_002");
        pcodes.add(1,"p_003");
        pcodes.add(2,"p_001");
        List<ResourceMenu> resourceMenus = menuMapper.selectList(new QueryWrapper<ResourceMenu>().lambda()
                .in(ResourceMenu::getPrivilegeCode, pcodes).groupBy(ResourceMenu::getApplicationCode));
        if(!resourceMenus.isEmpty()){
                for(ResourceMenu m:resourceMenus){
                    System.out.println(m);
                }
        }
    }

    @Test
    public void test10(){
        Integer pageNo = 1;
        Integer pageSize=2;
        ApplicationQueryParams query = new ApplicationQueryParams();
        query.setName("app");
        Page<ApplicationDTO> page = new Page<>(pageNo,pageSize);// 当前页，总条数 构造 page 对象
        List<ApplicationDTO> apps = applicationMapper.selectAppByPage(page, query);
        page.setRecords(apps);

    }
    @Test
    public void test09(){
        Integer pageNo = 2;
        Integer pageSize=2;
        MenuQueryDTO params = new MenuQueryDTO();
        params.setApplicationCode("a_001");
        params.setStatus(1);
        // Page<ResourceMenu> p = new Page<>(pageNo,pageSize);
        //List<MenuDTO> menuDTOS = menuMapper.selectByParam(params, p);
        //System.out.println(menuDTOS);
        //封装成PageVO
        //IPage<ResourceMenu> page = menuMapper.selectPage(p, null);
        //QueryWrapper<MenuDTO> qw = new QueryWrapper<>();

        //Page<ResourceMenu> page1 = new Page<>(pageNo,pageSize);// 当前页，总条数 构造 page 对象
        //List<ResourceMenu> menus = menuMapper.selectByPage(page1, params);
        //List<MenuDTO> menuDTOS = ResourceMenuConvert.INSTANCE.entitylist2dto(menus);
        //page1.setRecords(menus);
        //System.out.println(page1.toString());
        Page<MenuDTO> page = new Page<>(pageNo,pageSize);// 当前页，总条数 构造 page 对象
        List<MenuDTO> menus = menuMapper.selectByPage(page, params);
        page.setRecords(menus);
        System.out.println(page);
        //参数 applicationCode（app表） title status（菜单表）
        //System.out.println(page);
        //System.out.println("SIZE"+page.getSize()+"\n总条数"+page.getTotal()+"\n分页数"+page.getPages()+"\n当前页"+page.getCurrent()+"\n每页条数"+page.getSize()+"\n记录"+page.getRecords());
    }

    @Test
    public void test08() {
        String[] roleCodes = {"r_006"};
        List<String> roleList = new ArrayList<>(Arrays.asList(roleCodes));
        roleList.add("r_007");

        //Map<String, Object> map = new HashMap<>();
        //map.put("username","员工A");
        //map.put("tenantId",1151761810982985002L);
        //map.put("roleList",roleList);

        String username = "员工A";
        Long tenantId = 1151761810982985002L;

        //批量插入
        // System.out.println(map);
        accountRoleMapper.insertAccountRole(username, tenantId, roleList);
    }
    //public static void main(String[] args) {
    //    RoleDTO role = new RoleDTO();
    //    List<String> privilege = new ArrayList<>();
    //    privilege.add(0,"p1");
    //    privilege.add(1,"p2");
    //    role.setCode("r_002");
    //    role.setName("啊啊啊");
    //    role.setTenantId(123123123L);
    //    role.setPrivilegeCodes(privilege);
    //    AuthorizationRole entity = AuthorizationRoleConvert.INSTANCE.dto2entity(role);
    //    System.out.println(entity);
    //}
    //
    //@Test
    //public void tests() {
    //    AuthorizationRole role = authorizationRoleMapper.selectOne(new QueryWrapper<AuthorizationRole>().lambda()
    //            .eq(AuthorizationRole::getTenantId, 1151761810982985002L).eq(AuthorizationRole::getCode, "r_002"));
    //    System.out.println("-------------"+role);
    //    List<String> privilege = authorizationRoleMapper.selectPrivilegeByRole(1211761810982985002L);
    //
    //    RoleDTO roleDTO = AuthorizationRoleConvert.INSTANCE.entity2dto(role);
    //    roleDTO.setPrivilegeCodes(privilege);
    //    System.out.println("###########"+roleDTO);
    //
    //    List<RoleDTO> roleDTOS = new ArrayList<>();
    //    roleDTOS.add(roleDTO);
    //    System.out.println("=================="+roleDTOS);
    //
    //}
    //
    //@Test
    //public void test02(){
    //    QueryWrapper<AuthorizationRole> qw = new QueryWrapper<>();
    //    qw.lambda().eq(AuthorizationRole::getTenantId,1151761810982985002L);
    //    List<AuthorizationRole> authorizationRoles = authorizationRoleMapper.selectList(qw);
    //    System.out.println(authorizationRoles);
    //}
    //
    //@Test
    //public void test03(){
    //
    //    List<AuthorizationRole> authorizationRoles = authorizationRoleMapper.selectList(new QueryWrapper<AuthorizationRole>().lambda()
    //            .eq(AuthorizationRole::getTenantId,1151761810982985002L)
    //            .inSql(AuthorizationRole::getCode,));
    //    List<RoleDTO> roleDTOS = AuthorizationRoleConvert.INSTANCE.entitylist2dto(authorizationRoles);
    //}

    //@Test
    //public void test04(){
    //    String[] strs = {"r_002","r_003",""};
    //    List<String> codes= Arrays.asList(strs);
    //    List<AuthorizationRole> authorizationRoles = authorizationRoleMapper.selectList(new QueryWrapper<AuthorizationRole>().lambda()
    //            .eq(AuthorizationRole::getTenantId,1151761810982985002L)
    //            .in(AuthorizationRole::getCode,codes));
    //    List<RoleDTO> roleDTOS = AuthorizationRoleConvert.INSTANCE.entitylist2dto(authorizationRoles);
    //    System.out.println(roleDTOS);
    //}
    //public static void main(String[] args) {
    //
    //    String[] strs = {"r_002","r_003",""};
    //    List<String> codes= Arrays.asList(strs);
    //    System.out.println(codes);
    //    Long[] tenantIds ={1151761810982985002L,1151761810982985003L};
    //    List<Long> ids= Arrays.asList(tenantIds);
    //    System.out.println(ids);
    //
    //}

    @Test
    public void test05() {
        Long[] tenantIds = {1151761810982985002L, 1151761810982985003L};
        List<Long> ids = Arrays.asList(tenantIds);
        System.out.println(ids);
        //String[] aaa = {"1151761810982985002","1151761810982985003"};
        //System.out.println(aaa);
        List<Long> roleIds = accountRoleMapper.selectRoleByUsernameInTenants("员工A", ids);
        System.out.println("------------" + roleIds);

        List<TenRolePrivilegeDTO> list = authorizationPrivilegeMapper.selectPrivilegeRoleInTenant(roleIds);
/*        Map<String, String[]> temp = new HashMap<>();
        List<String> tempList = new ArrayList<>();
        for (TenRolePrivilegeDTO x : list) {
            String z = x.getRoleCode() + x.getTenantId();
            if (tempList.contains(z)) {
                continue;
            }
            List<String> xx = new ArrayList<>();
            for (TenRolePrivilegeDTO y : list) {

                if ((y.getRoleCode() + y.getTenantId()).equals(z)) {
                    xx.add(y.getPrivilegeCode());
                }

            }
            temp.put(x.getRoleCode(), xx.toArray(new String[xx.size()]));
        }*/
        Map<Long, AuthorizationInfoDTO> map = new HashMap<>();
        //AuthorizationInfoDTO auth = new AuthorizationInfoDTO();
   /*  [{PRIVILEGE_CODE=p1, TENANT_ID=1, ROLE_CODE=r2},
        {PRIVILEGE_CODE=p3, TENANT_ID=1, ROLE_CODE=r2},
        {PRIVILEGE_CODE=p6, TENANT_ID=2, ROLE_CODE=r5},
        {PRIVILEGE_CODE=p5, TENANT_ID=2, ROLE_CODE=r5}]*/

        //Map<Long,Map<String,String[]>> bb= new HashMap<>();

        //Map<String, String[]> temp = new HashMap<>();
        //List<String> tempList = new ArrayList<>();
        for (TenRolePrivilegeDTO dto : list) {
            if(!map.containsKey(dto.getTenantId())) {
                map.put(dto.getTenantId(), new AuthorizationInfoDTO());
            }
            AuthorizationInfoDTO info = map.get(dto.getTenantId());
            if(info.getRolePrivilegeMap().containsKey(dto.getRoleCode())) {
                info.getRolePrivilegeMap().get(dto.getRoleCode()).add(dto.getPrivilegeCode());
            } else {
                List<String> prviList = new ArrayList<>();
                prviList.add(dto.getPrivilegeCode());
                info.getRolePrivilegeMap().put(dto.getRoleCode(), prviList);

            }
        }

        //System.out.println(temp);
         System.out.println(map);
    }

    //temp.forEach((key, value) -> {
    //    System.out.println(key + ":" + value);
    //});
    //for (temp.Entry<Integer, Integer> entry : map.entrySet()) {
    //    System.out.println("key = " + entry.getKey());
    //}



    //Map<String,String[]>
        //Map<Long, AuthorizationInfoDTO>
      /*List<Menu> menuList = new ArrayList<Menu>();
       for (int i = 0; i < rootMenu.size(); i++) {
            // 一级菜单没有parentId
            if (StringUtils.isBlank(rootMenu.get(i).getParentId())) {
                menuList.add(rootMenu.get(i));
            }
        }
        // 为一级菜单设置子菜单，getChild是递归调用的
        for (Menu menu : menuList) {
            menu.setChildMenus(getChild(menu.getId(), rootMenu));
        }*//*
        //for (int i = 0; i <list.size() ; i++) {
        //    System.out.println(list.get(i));
        //    String[] split = list.get(i).split(",");
        //    System.out.println(split);
        //
        //}
        //Map<String,String[]> jsonMap = new HashMap<>();
        ////jsonMap.put("menu", menuList);
        //jsonMap.put();
        //System.out.println(JSON.toJSON(jsonMap));
        //
        //AuthorizationInfoDTO a = new AuthorizationInfoDTO();
        System.out.println(list);
    }



        //List<TenantDTO> tenantDTOS = tenantMapper.selectAccountInTenant(1171761810982985004L);



    //public static void main(String[] args) {
    //            /*        [TenRolePrivilegeDTO(tenantId=1151761810982985002, roleCode=r_002, privilegeCode=p_002),
    //            TenRolePrivilegeDTO(tenantId=1151761810982985002, roleCode=r_002, privilegeCode=p_003),
    //            TenRolePrivilegeDTO(tenantId=1151761810982985003, roleCode=r_005, privilegeCode=p_005),
    //            TenRolePrivilegeDTO(tenantId=1151761810982985003, roleCode=r_005, privilegeCode=p_006)]*/
        //    List<TenRolePrivilegeDTO> list = new ArrayList<>();
        //    for (int i = 0; i <list.size() ; i++) {
        //        System.out.println(list.get(i));
        //
        //    }
        //
        //}
   /* public static void main(String[] args) {
        String [] sum = new String[6];
        //第一种写法,注意事项是：括号里必须标明数组的长度，不能写成String [] sum = new String[];
        String [] sum1 = new String []{"张山","历史","你好","哈哈"};
        //第二种写法。
        String [] sum2 = {"张山","历史","你好","哈哈"};
        //第三种写法。
        int[] m = { 1, 2, 3 };
        String[] strings = { "aaa", "bbb" };
        List<String> list = new ArrayList<String>();
        List<Integer> lists = new ArrayList<Integer>();
        List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
        //List<City> listcity = new ArrayList<City>();

//        [{PRIVILEGE_CODE=p_002, TENANT_ID=1151761810982985002, ROLE_CODE=r_002},
//        {PRIVILEGE_CODE=p_003, TENANT_ID=1151761810982985002, ROLE_CODE=r_002},
//        {PRIVILEGE_CODE=p_006, TENANT_ID=1151761810982985003, ROLE_CODE=r_005},
//        {PRIVILEGE_CODE=p_005, TENANT_ID=1151761810982985003, ROLE_CODE=r_005}]
    }*/

 /*   @Test
    public void test06(){
        IPage<ResourceApplication> app = applicationMapper.selectPage(new Page<>(0, 5),
                new QueryWrapper<ResourceApplication>().lambda().like(ResourceApplication::getName, "app01"));
        System.out.println(app.toString());
    }*/

    //public static void main(String[] args) {

       /* List<String> pcodes1 = new ArrayList<>();
        pcodes1.add(0,"p001");
        pcodes1.add(1,"p002");
        List<String> pcodes2 = new ArrayList<>();
        pcodes2.add(0,"p005");
        pcodes2.add(1,"p006");

        Map<String,List<String>>  rolePrivilegeMap1 = new HashMap<>();
        rolePrivilegeMap1.put("r001",pcodes1);
        Map<String,List<String>>  rolePrivilegeMap2 = new HashMap<>();
        rolePrivilegeMap2.put("r002",pcodes2);

        AuthorizationInfoDTO dto1 = new AuthorizationInfoDTO();
        dto1.setRolePrivilegeMap(rolePrivilegeMap1);

        AuthorizationInfoDTO dto2 = new AuthorizationInfoDTO();
        dto2.setRolePrivilegeMap(rolePrivilegeMap2);

        Map<Long, AuthorizationInfoDTO> map = new HashMap<>();
        map.put(111L,dto1);
        map.put(222L,dto2);
        //List<String> value3= new ArrayList<>();
        //for (Map.Entry<Long, AuthorizationInfoDTO> entry : map.entrySet()) {
        //    System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        //    AuthorizationInfoDTO value = entry.getValue();
        //    System.out.println(value);
        //    Map<String, List<String>> rolePrivilegeMap = value.getRolePrivilegeMap();
        //    for (Map.Entry<String, List<String>> entry2 : rolePrivilegeMap.entrySet()){
        //        List<String> value1 = entry2.getValue();
        //        value3.addAll(value1);
        //    }
        //
        //}
        //System.out.println(value3);

        System.out.println(map);
        if(map.isEmpty()) {
            throw new BusinessException(CommonErrorCode.E_200202);
        }
        Map<Long, List<ResourceDTO>> resultResource = new HashMap<>();
        Map<String, List<ResourceDTO>> tmpMap = new HashMap<>();

        //获取多个租户的权限
        for (Map.Entry<Long, AuthorizationInfoDTO> entry : map.entrySet()) {
            AuthorizationInfoDTO dto = entry.getValue();//结构：rolecode1 [pcode1,pcode2]
            Map<String, List<String>> rolePrivilegeMap = dto.getRolePrivilegeMap();
            Set<String> privCodeSet = new HashSet<>();
            for (Map.Entry<String, List<String>> entry2 : rolePrivilegeMap.entrySet()) {
                List<String> pcodelist = entry2.getValue();//租户某个角色下的权限
                //根据权限获取资源（此处为资源的一种类型：menu）
                privCodeSet.addAll(pcodelist);
            }
            //查询到权限对应的资源和应用
            List<ResourceAPPDTO> result = menuMapper.selectResource(new ArrayList<>(privCodeSet));
            List<ResourceMenu> obj = new ArrayList<>();

            for (ResourceAPPDTO resApp : result) {
                if(tmpMap.containsKey(entry.getKey() + "" + resApp.getApplicationCode())) {
                    ResourceDTO resourceDTO = new ResourceDTO();
                    resourceDTO.setApplicationCode(resApp.getApplicationCode());
                    resourceDTO.setApplicationName(resApp.getName());

                }
            }


        }*/


    //}




    }
