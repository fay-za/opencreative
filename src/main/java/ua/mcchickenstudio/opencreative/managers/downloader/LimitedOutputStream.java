/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.managers.downloader;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class LimitedOutputStream extends FilterOutputStream {

    private final long maxBytes;
    private long written = 0;

    public LimitedOutputStream(int limit, OutputStream out) {
        super(out);
        this.maxBytes = limit * 1024L * 1024L;
    }

    private void checkLimit(long bytesToWrite) {
        if (written + bytesToWrite > maxBytes) {
            throw new TooBigWorldException(maxBytes/1024/1024);
        }
    }

    @Override
    public void write(int b) throws IOException {
        checkLimit(1);
        out.write(b);
        written++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        checkLimit(len);
        out.write(b, off, len);
        written += len;
    }

    public long getWritten() {
        return written;
    }
}
