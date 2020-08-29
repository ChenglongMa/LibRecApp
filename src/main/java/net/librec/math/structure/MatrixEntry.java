// Copyright (C) 2014 Guibing Guo
//
// This file is part of LibRec.
//
// LibRec is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LibRec is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a clone of the GNU General Public License
// along with LibRec. If not, see <http://www.gnu.org/licenses/>.
//

package net.librec.math.structure;

/**
 * An entry of a matrix.
 *
 * @author Keqiang Wang (email: sei.wkq2008@gmail.com)
 */
public interface MatrixEntry {

    /**
     * Returns the current row index
     *
     * @return the current row index
     */
    int row();

    /**
     * Returns the current column index
     *
     * @return the current column index
     */
    int column();

    /**
     * Returns the value at the current index
     *
     * @return the value at the current index
     */
    double get();

    /**
     * Sets the value at the current index
     *
     * @param value the value to set
     */
    void set(double value);

    int rowPosition();

    int columnPosition();
}
