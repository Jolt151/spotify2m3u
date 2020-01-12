package com.test

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class SiteController {
    @RequestMapping("/")
    fun home() = "index.html"
}