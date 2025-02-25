package org.api_sync.adapter.output;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ofertas-promociones/checks")
public class CheckController {

    @GetMapping
    public String get(@RequestParam(value = "empresa") String empresa,
                      @RequestParam(value = "sucursal") Integer sucursal) {
        return "Hola mundo";
    }

    @PostMapping
    public String post(@RequestParam(value = "empresa") String empresa,
                       @RequestParam(value = "sucursal") Integer sucursal) {
        return "";
    }


}
