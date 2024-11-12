package com.example.mycustomer.Controller;

import com.example.mycustomer.dto.CustomerDTO;
import com.example.mycustomer.mapper.CustomerMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerMapper mapper;

        //홈
        @GetMapping("/home")
        public String home() {
            return "customer/home";
        }


        //회원 가입 폼 요청
        @GetMapping("/join")
        public String join() {
            log.info("회원가입");
            return "customer/join";
        }

        //회원가입 처리 요청
        @PostMapping("/join")
        public String joinPro(CustomerDTO customerDTO) {
            log.info("customerDTO: {}", customerDTO);
            mapper.insertCustomer(customerDTO);
            return  "customer/login";
        }

    // id 중복확인 팝업 요청
    @GetMapping("/idAvail")
    public String idAvail(String id, Model model) {
        // 전달받은 id값이 DB에 존재하는지 여부 판단해서 view에 결과 전달
        boolean result = true; // 초기값은 사용가능으로 넣어놓고 밑에서 판단
        CustomerDTO customerDTO = mapper.selectOne(id);
        if(customerDTO != null) {
            result = false; // 이미 사용중 -> 사용 불가
        }
        // 이미 사용중이면 false, 사용가능한 id면 true
        model.addAttribute("result", result);

        return "customer/idAvail";
    }

        //로그인 폼 요청
        @GetMapping("/login")
        public String login() {
            return "customer/login";
        }


        //메인 페이지
        @RequestMapping("/home")
        public String home(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
            log.info("home");

            // 쿠키 있는지 검사
            Cookie[] cookies = request.getCookies();
            String cid = null, cpw = null, cauto = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) { // 쿠키 반복해 돌려서
                    // 각각 위 맞는 변수에 값 꺼내 체우기
                    if (cookie.getName().equals("cid")) { cid = cookie.getValue(); }
                    if (cookie.getName().equals("cpw")) { cpw = cookie.getValue(); }
                    if (cookie.getName().equals("cauto")) { cauto = cookie.getValue(); }
                }
            }
            if(cid != null && cpw != null && cauto != null) {
                // 로그인 처리 : session에 sid 추가, 쿠키 갱신
                CustomerDTO customerDTO = mapper.idPwCheck(cid, cpw);
                if(customerDTO != null) {
                    session.setAttribute("sid", cid);
                    // 쿠키 갱신(같은 쿠키 만들어 전송 )
                    createCookie(cid, cpw, cauto, response);
                }
            }
            return "customer/home";
        }
        // 쿠키 생성 메서드 분리
        private void createCookie(String id, String pw, String auto, HttpServletResponse response) {
            Cookie c1 = new Cookie("cid", id);  // 쿠키 객체 생성
            Cookie c2 = new Cookie("cpw", pw);
            Cookie c3 = new Cookie("cauto", auto);
            c1.setMaxAge(60 * 60); // 유효기간 설정 (60은 1분, 60*60 1시간, 60*60*24 1day)
            c2.setMaxAge(60 * 60);
            c3.setMaxAge(60 * 60);
            response.addCookie(c1); // 응답객체에 쿠키 추가 -> 브라우저에 전송
            response.addCookie(c2);
            response.addCookie(c3);
        }

        // 로그인 처리 요청
        @PostMapping("/login")
        public String loginPro(String id, String pw, String auto, HttpServletResponse response, HttpSession session, Model model) {
            log.info("POST /login 로그인처리!!!");
            log.info("id: {}", id);
            log.info("pw: {}", pw);
            log.info("auto: {}", auto); // 체크했으면 auto, 체크안했으면 null
            // 로그인 처리
            // DB에 사용자가 입력한 id와 pw가 일치하는 데이터가 있는지 확인
            boolean result = false;
            CustomerDTO customerDTO = mapper.idPwCheck(id, pw);
            if(customerDTO != null) {
                // 일치한다 -> 로그인 처리 -> 로그인 잘됐다는 결과 화면에 주기
                result = true; // 로그인결과 true로 지정
                session.setAttribute("sid", customerDTO.getId()); // 사용자 id값 저장
                if(auto != null) { // 자동로그인 체크 했다면,
                    createCookie(id, pw, auto, response);
                }
            }
            // 일치X -> 일치하지 않다는 결과 화면에 주기
            model.addAttribute("result", result); // 화면에 id,pw 검사결과 전달
            return "customer/loginPro"; // 로그인 결과 페이지
        }

        // 마이페이지 요청
        @GetMapping("/{id}") // ...8080/members/사용자id
        public String editMember(@PathVariable("id") String id) {
            log.info("mypage id: {}", id);
            return "customer/mypage";
        }

         //회원정보 수정 폼 요청
        @GetMapping("/{id}/modify")
        public String modify(@PathVariable("id") String id, Model model) {
            log.info("modify id: {}", id);
           CustomerDTO customerDTO = mapper.selectOne(id);
            model.addAttribute("customer",customerDTO);
            return "customer/modify";

        }

        //회원정보 수정 처리 요청
        @PostMapping("/{id}/modify")
        public String modifyPro(@PathVariable("id") String id,CustomerDTO customerDTO){
           customerDTO.setId(id);
          mapper.updateCustomer(customerDTO);
            return "redirect:/customer/{id}";
        }

        //회원목록 요청
        @GetMapping("/list")
        public String list(Model model) {
            List<CustomerDTO> list = mapper.selectAll();
            model.addAttribute("list", list);
            return "customer/list";
        }

        // 회원 탈퇴 폼 요청
        @GetMapping("/{id}/delete")
        public String deleteCustomer(@PathVariable("id") String id) {
            log.info("delete form - id: {}", id);
            return "customer/delete";
        }
        // 회원 탈퇴 처리 요청
        @PostMapping("/{id}/delete")
        public String deleteCustomerPro(@PathVariable("id") String id, String pw, Model model, HttpSession session) {
            log.info("deletePro id: {}", id);
            log.info("deletePro pw: {}", pw);
            // 탈퇴처리
            // id와 비번 맞는지 확인
            CustomerDTO customerDTO =mapper.idPwCheck(id, pw);
            boolean result = false;
            if(customerDTO != null) {
                // 맞으면 -> 탈퇴 처리 후 -> 결과 화면에 전달
                result = true;
                mapper.deleteCustomer(id); // 탈퇴
                session.invalidate();  // 로그아웃 처리

            }
            // 틀리면 -> 결과만 화면에 전달
            model.addAttribute("result", result);
            return "customer/deletePro";

        }

        //로그아웃
        @GetMapping("/logout")
        public String logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
            session.invalidate(); //세션 삭제
            //쿠키 삭제
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("cid") || cookie.getName().equals("cpw") || cookie.getName().equals("cauto")) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        return  "customer/home";
        }




    }





