/*
 * Copyright (c) 2021
 *
 * by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com) & DAW students
 *
 * TROLLEYES: Free Open Source Shopping Site
 *
 * Sources at:                https://github.com/rafaelaznar
 *
 * TROLLEYES is distributed under the MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.ausiasmarch.trolleyesSBserverJWT.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.trolleyesSBserverJWT.entity.FacturaEntity;
import net.ausiasmarch.trolleyesSBserverJWT.entity.UsuarioEntity;
import net.ausiasmarch.trolleyesSBserverJWT.repository.FacturaRepository;
import net.ausiasmarch.trolleyesSBserverJWT.repository.UsuarioRepository;
import net.ausiasmarch.trolleyesSBserverJWT.service.FillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/factura")

public class FacturaController {

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    FillService oFillService;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        FacturaEntity oFacturaEntity = new FacturaEntity();

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador
                if (oFacturaRepository.existsById(id)) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.getOne(id), HttpStatus.OK);
                } else {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.getOne(id), HttpStatus.NOT_FOUND);
                }
            } else {  //cliente
                oFacturaEntity = oFacturaRepository.getOne(id);
                if (oFacturaEntity != null) {
                    if (oFacturaEntity.getUsuario().getId().equals(oUsuarioEntity.getId())) {
                        return new ResponseEntity<FacturaEntity>(oFacturaRepository.getOne(id), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
//                if (oFacturaEntity.getUsuario().getId().equals(oUsuarioEntity.getId())) {  //los datos pedidos por el cliente son sus propios datos?
//                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.getOne(id), HttpStatus.OK);
//                } else {
//                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//                }
            }
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        if (oFacturaRepository.count() <= 1000) {
            return new ResponseEntity<List<FacturaEntity>>(oFacturaRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody FacturaEntity oFacturaEntity) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {

            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador

                if (oFacturaEntity.getId() == null) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.save(oFacturaEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }

            } else {  //cliente

                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {

            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador

                oFacturaRepository.deleteById(id);
                if (oFacturaRepository.existsById(id)) {
                    return new ResponseEntity<Long>(id, HttpStatus.NOT_MODIFIED);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.OK);
                }

            } else {  //cliente

                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }

    }

    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return new ResponseEntity<Long>(oFacturaRepository.count(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @RequestBody FacturaEntity oFacturaEntity) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {

            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador

                oFacturaEntity.setId(id);
                if (oFacturaRepository.existsById(id)) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.save(oFacturaEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }

            } else {  //cliente

                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }

    }

    @GetMapping("/page")
    public ResponseEntity<?> getPage(@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable oPageable) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
//        FacturaEntity oFacturaEntity = new FacturaEntity();

        Page<FacturaEntity> oPage = oFacturaRepository.findAll(oPageable);
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador
                return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {  //cliente
//                if (oFacturaEntity2.getUsuario().getId().equals(oUsuarioEntity.getId())) {  //los datos pedidos por el cliente son sus propios datos?
//                    return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
                return new ResponseEntity<Page<FacturaEntity>>(oFacturaRepository.findByFacturaXUsuario(oUsuarioEntity.getId(), oPageable), HttpStatus.OK);
//                } else {
//                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//                }
            }
        }
    }

    @GetMapping("/pagexusuario/{id}")
    public ResponseEntity<?> getPageXUsuario(@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable oPageable, @PathVariable(value = "id") Long id) {

        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            Page<FacturaEntity> oPage = oFacturaRepository.findByUsuario(oUsuarioEntity, oPageable);
            return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/allxusuario/{id}")
    public ResponseEntity<?> getAllXUsuario(@PathVariable(value = "id") Long id) {
        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            if (oUsuarioEntity.getTipousuario().getId() > 1) {
                List<FacturaEntity> oPage = oFacturaRepository.findByUsuario(oUsuarioEntity);
                return new ResponseEntity<List<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @PostMapping("/fill/{amount}")
    public ResponseEntity<?> fill(@PathVariable(value = "amount") Long amount) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {
                return new ResponseEntity<Long>(oFillService.facturaFill(amount), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping("/page/usuario/{id}")
    public ResponseEntity<?> getPageXusuario(@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable oPageable, @PathVariable(value = "id") Long id) {

        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            Page<FacturaEntity> oPage = oFacturaRepository.findByUsuario(oUsuarioEntity, oPageable);
            return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

    }

    //-----INFORMES---------
    @GetMapping("/allxusuario/10/{id}")
    public ResponseEntity<?> getAllXUsuario10(@PathVariable(value = "id") Long id) {
        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            if (oUsuarioEntity.getTipousuario().getId() > 1) {
                List<FacturaEntity> oPage = oFacturaRepository.findTop10ByUsuarioOrderByFechaDesc(oUsuarioEntity);
                return new ResponseEntity<List<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/allxusuario/10/{id}/{fini}/{ffin}")
    public ResponseEntity<?> getAllXUsuario10(@PathVariable(value = "id") Long id,
            @PathVariable(value = "fini") String fini,
            @PathVariable(value = "ffin") String ffin
    ) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime fromDate = LocalDate.parse(fini, fmt).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(ffin, fmt).atStartOfDay();

        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            if (oUsuarioEntity.getTipousuario().getId() > 1) {
                List<FacturaEntity> oPage = oFacturaRepository.findTop10ByUsuarioAndFechaGreaterThanEqualAndFechaLessThanEqualOrderByFechaDesc(oUsuarioEntity, fromDate, endDate);
                return new ResponseEntity<List<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/allxusuario/100/{id}/{fini}/{ffin}")
    public ResponseEntity<?> getAllXUsuario100(@PathVariable(value = "id") Long id,
            @PathVariable(value = "fini") String fini,
            @PathVariable(value = "ffin") String ffin
    ) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime fromDate = LocalDate.parse(fini, fmt).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(ffin, fmt).atStartOfDay();

        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            if (oUsuarioEntity.getTipousuario().getId() > 1) {
                List<FacturaEntity> oPage = oFacturaRepository.findTop100ByUsuarioAndFechaGreaterThanEqualAndFechaLessThanEqualOrderByFechaDesc(oUsuarioEntity, fromDate, endDate);
                return new ResponseEntity<List<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/allxusuario/1000/{id}/{fini}/{ffin}")
    public ResponseEntity<?> getAllXUsuario1000(@PathVariable(value = "id") Long id,
            @PathVariable(value = "fini") String fini,
            @PathVariable(value = "ffin") String ffin
    ) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime fromDate = LocalDate.parse(fini, fmt).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(ffin, fmt).atStartOfDay();

        if (oUsuarioRepository.existsById(id)) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.getOne(id);
            if (oUsuarioEntity.getTipousuario().getId() > 1) {
                List<FacturaEntity> oPage = oFacturaRepository.findTop1000ByUsuarioAndFechaGreaterThanEqualAndFechaLessThanEqualOrderByFechaDesc(oUsuarioEntity, fromDate, endDate);
                return new ResponseEntity<List<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

}
