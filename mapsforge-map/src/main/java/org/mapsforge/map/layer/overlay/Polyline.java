/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.layer.overlay;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

/**
 * A {@code Polyline} draws a connected series of line segments specified by a list of {@link LatLong LatLongs}.
 * <p>
 * A {@code Polyline} holds a {@link Paint} object which defines drawing parameters such as color, stroke width, pattern
 * and transparency.
 */
public class Polyline extends Layer {
	private final List<LatLong> latLongs = new CopyOnWriteArrayList<LatLong>();
	private Paint paintStroke;

	/**
	 * @param paintStroke
	 *            the initial {@code Paint} used to stroke this polyline (may be null).
	 */
	public Polyline(Paint paintStroke) {
		super();

		this.paintStroke = paintStroke;
	}

	@Override
	public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point canvasPosition) {
		if (this.latLongs.isEmpty() || this.paintStroke == null) {
			return;
		}

		Iterator<LatLong> iterator = this.latLongs.iterator();
		LatLong latLong = iterator.next();
		int previousX = (int) (MercatorProjection.longitudeToPixelX(latLong.longitude, zoomLevel) - canvasPosition.x);
		int previousY = (int) (MercatorProjection.latitudeToPixelY(latLong.latitude, zoomLevel) - canvasPosition.y);

		while (iterator.hasNext()) {
			latLong = iterator.next();
			int x = (int) (MercatorProjection.longitudeToPixelX(latLong.longitude, zoomLevel) - canvasPosition.x);
			int y = (int) (MercatorProjection.latitudeToPixelY(latLong.latitude, zoomLevel) - canvasPosition.y);

			canvas.drawLine(previousX, previousY, x, y, this.paintStroke);

			previousX = x;
			previousY = y;
		}
	}

	/**
	 * @return a thread-safe list of LatLongs in this polyline.
	 */
	public List<LatLong> getLatLongs() {
		return this.latLongs;
	}

	/**
	 * @return the {@code Paint} used to stroke this polyline (may be null).
	 */
	public synchronized Paint getPaintStroke() {
		return this.paintStroke;
	}

	/**
	 * @param paintStroke
	 *            the new {@code Paint} used to stroke this polyline (may be null).
	 */
	public synchronized void setPaintStroke(Paint paintStroke) {
		this.paintStroke = paintStroke;
	}
}