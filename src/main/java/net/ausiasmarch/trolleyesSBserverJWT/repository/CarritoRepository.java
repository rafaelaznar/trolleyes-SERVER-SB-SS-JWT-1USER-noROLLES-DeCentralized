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
package net.ausiasmarch.trolleyesSBserverJWT.repository;

import java.util.List;
import net.ausiasmarch.trolleyesSBserverJWT.entity.CarritoEntity;
import net.ausiasmarch.trolleyesSBserverJWT.entity.ProductoEntity;
import net.ausiasmarch.trolleyesSBserverJWT.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {

    @Query(value = "SELECT * FROM carrito c WHERE c.id_producto = :id_producto", nativeQuery = true)
    Page<CarritoEntity> findByCarritoXProducto(Long id_producto, Pageable pageable);

    Page<CarritoEntity> findByProducto(ProductoEntity oProductoEntity, Pageable oPageable);

    Page<CarritoEntity> findByProductoAndUsuario(ProductoEntity oProductoEntity, UsuarioEntity oUsuarioEntity, Pageable oPageable);

    @Query(value = "SELECT * FROM carrito c WHERE c.id_usuario = :id_usuario", nativeQuery = true)
    Page<CarritoEntity> findByCarritoXUsuario(Long id_usuario, Pageable pageable);

    Page<CarritoEntity> findByUsuario(UsuarioEntity oUsuarioEntity, Pageable oPageable);

    List<CarritoEntity> findAllByUsuario(UsuarioEntity oUsuarioEntity);

    Long countByUsuarioAndProducto(UsuarioEntity oUsuarioEntity, ProductoEntity oProductoEntity);

    @Query(value = "SELECT * FROM carrito c WHERE c.id_usuario = :id_usuario and c.id_producto = :id_producto", nativeQuery = true)
    CarritoEntity findByUsuarioAndProducto(Long id_usuario, Long id_producto);

    Long deleteByUsuario(UsuarioEntity oUsuarioEntity);

    Long deleteByIdAndUsuario(Long id, UsuarioEntity oUsuarioEntity);

    CarritoEntity findByIdAndUsuario(Long idCarrito, UsuarioEntity oUsuarioEntity);

}
