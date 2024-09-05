package com.biocore.DummyFingerMaker.service.impl;

public class SampleData {
        private byte[] isoData;
        private byte[] rawData;
        private int height;
        private int width;

        // Getters and setters
        public byte[] getIsoData() {
                return isoData;
        }

        public void setIsoData(byte[] isoData) {
                this.isoData = isoData;
        }

        public byte[] getRawData() {
                return rawData;
        }

        public void setRawData(byte[] rawData) {
                this.rawData = rawData;
        }

        public int getHeight() {
                return height;
        }

        public void setHeight(int height) {
                this.height = height;
        }

        public int getWidth() {
                return width;
        }

        public void setWidth(int width) {
                this.width = width;
        }


        @Override
        public String toString() {
                return "SampleData{" +
                        "height=" + height +
                        ", width=" + width +
                        ", isoDataLength=" + (isoData != null ? isoData.length : "No Data") +
                        ", rawDataLength=" + (rawData != null ? rawData.length : "No Data") +
                        '}';
        }
}
