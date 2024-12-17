/*
 * Copyright (c) 2009, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


package com.fleeksoft.charset.cs.jis

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte

class JIS_X_0212 : Charset("JIS_X0212-1990", null) {

    companion object {
        val INSTANCE = JIS_X_0212()
    }

    override fun contains(cs: Charset): Boolean {
        return (cs is JIS_X_0212)
    }

    override fun newDecoder(): CharsetDecoder {
        return DoubleByte.Decoder_DBCSONLY(
            this,
            DecodeHolder.b2c,
            DecodeHolder.b2cSB,
            0x21,
            0x7e,
            false
        )
    }

    override fun newEncoder(): CharsetEncoder {
        return DoubleByte.Encoder_DBCSONLY(
            this,
            byteArrayOf(0x22.toByte(), 0x44.toByte()),
            EncodeHolder.c2b,
            EncodeHolder.c2bIndex,
            false
        )
    }

    object DecodeHolder {
        const val b2cSBStr: String = "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD"

        val b2cStr: Array<String?> = arrayOf<String?>(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u02D8\u02C7" +
                    "\u00B8\u02D9\u02DD\u00AF\u02DB\u02DA\uFF5E\u0384" +
                    "\u0385\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\u00A1\u00A6\u00BF\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\u00BA\u00AA\u00A9\u00AE\u2122\u00A4" +
                    "\u2116\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD",
            null,
            null,
            null,
            "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\u0386\u0388\u0389\u038A\u03AA\uFFFD\u038C\uFFFD" +
                    "\u038E\u03AB\uFFFD\u038F\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\u03AC\u03AD\u03AE\u03AF\u03CA\u0390\u03CC\u03C2" +
                    "\u03CD\u03CB\u03B0\u03CE\uFFFD\uFFFD",
            "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\u0402\u0403\u0404\u0405\u0406\u0407\u0408" +
                    "\u0409\u040A\u040B\u040C\u040E\u040F\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\u0452\u0453\u0454\u0455\u0456\u0457\u0458" +
                    "\u0459\u045A\u045B\u045C\u045E\u045F",
            null,
            "\u00C6\u0110\uFFFD\u0126\uFFFD\u0132\uFFFD\u0141" +
                    "\u013F\uFFFD\u014A\u00D8\u0152\uFFFD\u0166\u00DE" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\u00E6\u0111\u00F0\u0127\u0131\u0133\u0138\u0142" +
                    "\u0140\u0149\u014B\u00F8\u0153\u00DF\u0167\u00FE" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD",
            "\u00C1\u00C0\u00C4\u00C2\u0102\u01CD\u0100\u0104" +
                    "\u00C5\u00C3\u0106\u0108\u010C\u00C7\u010A\u010E" +
                    "\u00C9\u00C8\u00CB\u00CA\u011A\u0116\u0112\u0118" +
                    "\uFFFD\u011C\u011E\u0122\u0120\u0124\u00CD\u00CC" +
                    "\u00CF\u00CE\u01CF\u0130\u012A\u012E\u0128\u0134" +
                    "\u0136\u0139\u013D\u013B\u0143\u0147\u0145\u00D1" +
                    "\u00D3\u00D2\u00D6\u00D4\u01D1\u0150\u014C\u00D5" +
                    "\u0154\u0158\u0156\u015A\u015C\u0160\u015E\u0164" +
                    "\u0162\u00DA\u00D9\u00DC\u00DB\u016C\u01D3\u0170" +
                    "\u016A\u0172\u016E\u0168\u01D7\u01DB\u01D9\u01D5" +
                    "\u0174\u00DD\u0178\u0176\u0179\u017D\u017B\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD",
            "\u00E1\u00E0\u00E4\u00E2\u0103\u01CE\u0101\u0105" +
                    "\u00E5\u00E3\u0107\u0109\u010D\u00E7\u010B\u010F" +
                    "\u00E9\u00E8\u00EB\u00EA\u011B\u0117\u0113\u0119" +
                    "\u01F5\u011D\u011F\uFFFD\u0121\u0125\u00ED\u00EC" +
                    "\u00EF\u00EE\u01D0\uFFFD\u012B\u012F\u0129\u0135" +
                    "\u0137\u013A\u013E\u013C\u0144\u0148\u0146\u00F1" +
                    "\u00F3\u00F2\u00F6\u00F4\u01D2\u0151\u014D\u00F5" +
                    "\u0155\u0159\u0157\u015B\u015D\u0161\u015F\u0165" +
                    "\u0163\u00FA\u00F9\u00FC\u00FB\u016D\u01D4\u0171" +
                    "\u016B\u0173\u016F\u0169\u01D8\u01DC\u01DA\u01D6" +
                    "\u0175\u00FD\u00FF\u0177\u017A\u017E\u017C\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD",
            null,
            null,
            null,
            null,
            "\u4E02\u4E04\u4E05\u4E0C\u4E12\u4E1F\u4E23\u4E24" +
                    "\u4E28\u4E2B\u4E2E\u4E2F\u4E30\u4E35\u4E40\u4E41" +
                    "\u4E44\u4E47\u4E51\u4E5A\u4E5C\u4E63\u4E68\u4E69" +
                    "\u4E74\u4E75\u4E79\u4E7F\u4E8D\u4E96\u4E97\u4E9D" +
                    "\u4EAF\u4EB9\u4EC3\u4ED0\u4EDA\u4EDB\u4EE0\u4EE1" +
                    "\u4EE2\u4EE8\u4EEF\u4EF1\u4EF3\u4EF5\u4EFD\u4EFE" +
                    "\u4EFF\u4F00\u4F02\u4F03\u4F08\u4F0B\u4F0C\u4F12" +
                    "\u4F15\u4F16\u4F17\u4F19\u4F2E\u4F31\u4F60\u4F33" +
                    "\u4F35\u4F37\u4F39\u4F3B\u4F3E\u4F40\u4F42\u4F48" +
                    "\u4F49\u4F4B\u4F4C\u4F52\u4F54\u4F56\u4F58\u4F5F" +
                    "\u4F63\u4F6A\u4F6C\u4F6E\u4F71\u4F77\u4F78\u4F79" +
                    "\u4F7A\u4F7D\u4F7E\u4F81\u4F82\u4F84",
            "\u4F85\u4F89\u4F8A\u4F8C\u4F8E\u4F90\u4F92\u4F93" +
                    "\u4F94\u4F97\u4F99\u4F9A\u4F9E\u4F9F\u4FB2\u4FB7" +
                    "\u4FB9\u4FBB\u4FBC\u4FBD\u4FBE\u4FC0\u4FC1\u4FC5" +
                    "\u4FC6\u4FC8\u4FC9\u4FCB\u4FCC\u4FCD\u4FCF\u4FD2" +
                    "\u4FDC\u4FE0\u4FE2\u4FF0\u4FF2\u4FFC\u4FFD\u4FFF" +
                    "\u5000\u5001\u5004\u5007\u500A\u500C\u500E\u5010" +
                    "\u5013\u5017\u5018\u501B\u501C\u501D\u501E\u5022" +
                    "\u5027\u502E\u5030\u5032\u5033\u5035\u5040\u5041" +
                    "\u5042\u5045\u5046\u504A\u504C\u504E\u5051\u5052" +
                    "\u5053\u5057\u5059\u505F\u5060\u5062\u5063\u5066" +
                    "\u5067\u506A\u506D\u5070\u5071\u503B\u5081\u5083" +
                    "\u5084\u5086\u508A\u508E\u508F\u5090",
            "\u5092\u5093\u5094\u5096\u509B\u509C\u509E\u509F" +
                    "\u50A0\u50A1\u50A2\u50AA\u50AF\u50B0\u50B9\u50BA" +
                    "\u50BD\u50C0\u50C3\u50C4\u50C7\u50CC\u50CE\u50D0" +
                    "\u50D3\u50D4\u50D8\u50DC\u50DD\u50DF\u50E2\u50E4" +
                    "\u50E6\u50E8\u50E9\u50EF\u50F1\u50F6\u50FA\u50FE" +
                    "\u5103\u5106\u5107\u5108\u510B\u510C\u510D\u510E" +
                    "\u50F2\u5110\u5117\u5119\u511B\u511C\u511D\u511E" +
                    "\u5123\u5127\u5128\u512C\u512D\u512F\u5131\u5133" +
                    "\u5134\u5135\u5138\u5139\u5142\u514A\u514F\u5153" +
                    "\u5155\u5157\u5158\u515F\u5164\u5166\u517E\u5183" +
                    "\u5184\u518B\u518E\u5198\u519D\u51A1\u51A3\u51AD" +
                    "\u51B8\u51BA\u51BC\u51BE\u51BF\u51C2",
            "\u51C8\u51CF\u51D1\u51D2\u51D3\u51D5\u51D8\u51DE" +
                    "\u51E2\u51E5\u51EE\u51F2\u51F3\u51F4\u51F7\u5201" +
                    "\u5202\u5205\u5212\u5213\u5215\u5216\u5218\u5222" +
                    "\u5228\u5231\u5232\u5235\u523C\u5245\u5249\u5255" +
                    "\u5257\u5258\u525A\u525C\u525F\u5260\u5261\u5266" +
                    "\u526E\u5277\u5278\u5279\u5280\u5282\u5285\u528A" +
                    "\u528C\u5293\u5295\u5296\u5297\u5298\u529A\u529C" +
                    "\u52A4\u52A5\u52A6\u52A7\u52AF\u52B0\u52B6\u52B7" +
                    "\u52B8\u52BA\u52BB\u52BD\u52C0\u52C4\u52C6\u52C8" +
                    "\u52CC\u52CF\u52D1\u52D4\u52D6\u52DB\u52DC\u52E1" +
                    "\u52E5\u52E8\u52E9\u52EA\u52EC\u52F0\u52F1\u52F4" +
                    "\u52F6\u52F7\u5300\u5303\u530A\u530B",
            "\u530C\u5311\u5313\u5318\u531B\u531C\u531E\u531F" +
                    "\u5325\u5327\u5328\u5329\u532B\u532C\u532D\u5330" +
                    "\u5332\u5335\u533C\u533D\u533E\u5342\u534C\u534B" +
                    "\u5359\u535B\u5361\u5363\u5365\u536C\u536D\u5372" +
                    "\u5379\u537E\u5383\u5387\u5388\u538E\u5393\u5394" +
                    "\u5399\u539D\u53A1\u53A4\u53AA\u53AB\u53AF\u53B2" +
                    "\u53B4\u53B5\u53B7\u53B8\u53BA\u53BD\u53C0\u53C5" +
                    "\u53CF\u53D2\u53D3\u53D5\u53DA\u53DD\u53DE\u53E0" +
                    "\u53E6\u53E7\u53F5\u5402\u5413\u541A\u5421\u5427" +
                    "\u5428\u542A\u542F\u5431\u5434\u5435\u5443\u5444" +
                    "\u5447\u544D\u544F\u545E\u5462\u5464\u5466\u5467" +
                    "\u5469\u546B\u546D\u546E\u5474\u547F",
            "\u5481\u5483\u5485\u5488\u5489\u548D\u5491\u5495" +
                    "\u5496\u549C\u549F\u54A1\u54A6\u54A7\u54A9\u54AA" +
                    "\u54AD\u54AE\u54B1\u54B7\u54B9\u54BA\u54BB\u54BF" +
                    "\u54C6\u54CA\u54CD\u54CE\u54E0\u54EA\u54EC\u54EF" +
                    "\u54F6\u54FC\u54FE\u54FF\u5500\u5501\u5505\u5508" +
                    "\u5509\u550C\u550D\u550E\u5515\u552A\u552B\u5532" +
                    "\u5535\u5536\u553B\u553C\u553D\u5541\u5547\u5549" +
                    "\u554A\u554D\u5550\u5551\u5558\u555A\u555B\u555E" +
                    "\u5560\u5561\u5564\u5566\u557F\u5581\u5582\u5586" +
                    "\u5588\u558E\u558F\u5591\u5592\u5593\u5594\u5597" +
                    "\u55A3\u55A4\u55AD\u55B2\u55BF\u55C1\u55C3\u55C6" +
                    "\u55C9\u55CB\u55CC\u55CE\u55D1\u55D2",
            "\u55D3\u55D7\u55D8\u55DB\u55DE\u55E2\u55E9\u55F6" +
                    "\u55FF\u5605\u5608\u560A\u560D\u560E\u560F\u5610" +
                    "\u5611\u5612\u5619\u562C\u5630\u5633\u5635\u5637" +
                    "\u5639\u563B\u563C\u563D\u563F\u5640\u5641\u5643" +
                    "\u5644\u5646\u5649\u564B\u564D\u564F\u5654\u565E" +
                    "\u5660\u5661\u5662\u5663\u5666\u5669\u566D\u566F" +
                    "\u5671\u5672\u5675\u5684\u5685\u5688\u568B\u568C" +
                    "\u5695\u5699\u569A\u569D\u569E\u569F\u56A6\u56A7" +
                    "\u56A8\u56A9\u56AB\u56AC\u56AD\u56B1\u56B3\u56B7" +
                    "\u56BE\u56C5\u56C9\u56CA\u56CB\u56CF\u56D0\u56CC" +
                    "\u56CD\u56D9\u56DC\u56DD\u56DF\u56E1\u56E4\u56E5" +
                    "\u56E6\u56E7\u56E8\u56F1\u56EB\u56ED",
            "\u56F6\u56F7\u5701\u5702\u5707\u570A\u570C\u5711" +
                    "\u5715\u571A\u571B\u571D\u5720\u5722\u5723\u5724" +
                    "\u5725\u5729\u572A\u572C\u572E\u572F\u5733\u5734" +
                    "\u573D\u573E\u573F\u5745\u5746\u574C\u574D\u5752" +
                    "\u5762\u5765\u5767\u5768\u576B\u576D\u576E\u576F" +
                    "\u5770\u5771\u5773\u5774\u5775\u5777\u5779\u577A" +
                    "\u577B\u577C\u577E\u5781\u5783\u578C\u5794\u5797" +
                    "\u5799\u579A\u579C\u579D\u579E\u579F\u57A1\u5795" +
                    "\u57A7\u57A8\u57A9\u57AC\u57B8\u57BD\u57C7\u57C8" +
                    "\u57CC\u57CF\u57D5\u57DD\u57DE\u57E4\u57E6\u57E7" +
                    "\u57E9\u57ED\u57F0\u57F5\u57F6\u57F8\u57FD\u57FE" +
                    "\u57FF\u5803\u5804\u5808\u5809\u57E1",
            "\u580C\u580D\u581B\u581E\u581F\u5820\u5826\u5827" +
                    "\u582D\u5832\u5839\u583F\u5849\u584C\u584D\u584F" +
                    "\u5850\u5855\u585F\u5861\u5864\u5867\u5868\u5878" +
                    "\u587C\u587F\u5880\u5881\u5887\u5888\u5889\u588A" +
                    "\u588C\u588D\u588F\u5890\u5894\u5896\u589D\u58A0" +
                    "\u58A1\u58A2\u58A6\u58A9\u58B1\u58B2\u58C4\u58BC" +
                    "\u58C2\u58C8\u58CD\u58CE\u58D0\u58D2\u58D4\u58D6" +
                    "\u58DA\u58DD\u58E1\u58E2\u58E9\u58F3\u5905\u5906" +
                    "\u590B\u590C\u5912\u5913\u5914\u8641\u591D\u5921" +
                    "\u5923\u5924\u5928\u592F\u5930\u5933\u5935\u5936" +
                    "\u593F\u5943\u5946\u5952\u5953\u5959\u595B\u595D" +
                    "\u595E\u595F\u5961\u5963\u596B\u596D",
            "\u596F\u5972\u5975\u5976\u5979\u597B\u597C\u598B" +
                    "\u598C\u598E\u5992\u5995\u5997\u599F\u59A4\u59A7" +
                    "\u59AD\u59AE\u59AF\u59B0\u59B3\u59B7\u59BA\u59BC" +
                    "\u59C1\u59C3\u59C4\u59C8\u59CA\u59CD\u59D2\u59DD" +
                    "\u59DE\u59DF\u59E3\u59E4\u59E7\u59EE\u59EF\u59F1" +
                    "\u59F2\u59F4\u59F7\u5A00\u5A04\u5A0C\u5A0D\u5A0E" +
                    "\u5A12\u5A13\u5A1E\u5A23\u5A24\u5A27\u5A28\u5A2A" +
                    "\u5A2D\u5A30\u5A44\u5A45\u5A47\u5A48\u5A4C\u5A50" +
                    "\u5A55\u5A5E\u5A63\u5A65\u5A67\u5A6D\u5A77\u5A7A" +
                    "\u5A7B\u5A7E\u5A8B\u5A90\u5A93\u5A96\u5A99\u5A9C" +
                    "\u5A9E\u5A9F\u5AA0\u5AA2\u5AA7\u5AAC\u5AB1\u5AB2" +
                    "\u5AB3\u5AB5\u5AB8\u5ABA\u5ABB\u5ABF",
            "\u5AC4\u5AC6\u5AC8\u5ACF\u5ADA\u5ADC\u5AE0\u5AE5" +
                    "\u5AEA\u5AEE\u5AF5\u5AF6\u5AFD\u5B00\u5B01\u5B08" +
                    "\u5B17\u5B34\u5B19\u5B1B\u5B1D\u5B21\u5B25\u5B2D" +
                    "\u5B38\u5B41\u5B4B\u5B4C\u5B52\u5B56\u5B5E\u5B68" +
                    "\u5B6E\u5B6F\u5B7C\u5B7D\u5B7E\u5B7F\u5B81\u5B84" +
                    "\u5B86\u5B8A\u5B8E\u5B90\u5B91\u5B93\u5B94\u5B96" +
                    "\u5BA8\u5BA9\u5BAC\u5BAD\u5BAF\u5BB1\u5BB2\u5BB7" +
                    "\u5BBA\u5BBC\u5BC0\u5BC1\u5BCD\u5BCF\u5BD6\u5BD7" +
                    "\u5BD8\u5BD9\u5BDA\u5BE0\u5BEF\u5BF1\u5BF4\u5BFD" +
                    "\u5C0C\u5C17\u5C1E\u5C1F\u5C23\u5C26\u5C29\u5C2B" +
                    "\u5C2C\u5C2E\u5C30\u5C32\u5C35\u5C36\u5C59\u5C5A" +
                    "\u5C5C\u5C62\u5C63\u5C67\u5C68\u5C69",
            "\u5C6D\u5C70\u5C74\u5C75\u5C7A\u5C7B\u5C7C\u5C7D" +
                    "\u5C87\u5C88\u5C8A\u5C8F\u5C92\u5C9D\u5C9F\u5CA0" +
                    "\u5CA2\u5CA3\u5CA6\u5CAA\u5CB2\u5CB4\u5CB5\u5CBA" +
                    "\u5CC9\u5CCB\u5CD2\u5CDD\u5CD7\u5CEE\u5CF1\u5CF2" +
                    "\u5CF4\u5D01\u5D06\u5D0D\u5D12\u5D2B\u5D23\u5D24" +
                    "\u5D26\u5D27\u5D31\u5D34\u5D39\u5D3D\u5D3F\u5D42" +
                    "\u5D43\u5D46\u5D48\u5D55\u5D51\u5D59\u5D4A\u5D5F" +
                    "\u5D60\u5D61\u5D62\u5D64\u5D6A\u5D6D\u5D70\u5D79" +
                    "\u5D7A\u5D7E\u5D7F\u5D81\u5D83\u5D88\u5D8A\u5D92" +
                    "\u5D93\u5D94\u5D95\u5D99\u5D9B\u5D9F\u5DA0\u5DA7" +
                    "\u5DAB\u5DB0\u5DB4\u5DB8\u5DB9\u5DC3\u5DC7\u5DCB" +
                    "\u5DD0\u5DCE\u5DD8\u5DD9\u5DE0\u5DE4",
            "\u5DE9\u5DF8\u5DF9\u5E00\u5E07\u5E0D\u5E12\u5E14" +
                    "\u5E15\u5E18\u5E1F\u5E20\u5E2E\u5E28\u5E32\u5E35" +
                    "\u5E3E\u5E4B\u5E50\u5E49\u5E51\u5E56\u5E58\u5E5B" +
                    "\u5E5C\u5E5E\u5E68\u5E6A\u5E6B\u5E6C\u5E6D\u5E6E" +
                    "\u5E70\u5E80\u5E8B\u5E8E\u5EA2\u5EA4\u5EA5\u5EA8" +
                    "\u5EAA\u5EAC\u5EB1\u5EB3\u5EBD\u5EBE\u5EBF\u5EC6" +
                    "\u5ECC\u5ECB\u5ECE\u5ED1\u5ED2\u5ED4\u5ED5\u5EDC" +
                    "\u5EDE\u5EE5\u5EEB\u5F02\u5F06\u5F07\u5F08\u5F0E" +
                    "\u5F19\u5F1C\u5F1D\u5F21\u5F22\u5F23\u5F24\u5F28" +
                    "\u5F2B\u5F2C\u5F2E\u5F30\u5F34\u5F36\u5F3B\u5F3D" +
                    "\u5F3F\u5F40\u5F44\u5F45\u5F47\u5F4D\u5F50\u5F54" +
                    "\u5F58\u5F5B\u5F60\u5F63\u5F64\u5F67",
            "\u5F6F\u5F72\u5F74\u5F75\u5F78\u5F7A\u5F7D\u5F7E" +
                    "\u5F89\u5F8D\u5F8F\u5F96\u5F9C\u5F9D\u5FA2\u5FA7" +
                    "\u5FAB\u5FA4\u5FAC\u5FAF\u5FB0\u5FB1\u5FB8\u5FC4" +
                    "\u5FC7\u5FC8\u5FC9\u5FCB\u5FD0\u5FD1\u5FD2\u5FD3" +
                    "\u5FD4\u5FDE\u5FE1\u5FE2\u5FE8\u5FE9\u5FEA\u5FEC" +
                    "\u5FED\u5FEE\u5FEF\u5FF2\u5FF3\u5FF6\u5FFA\u5FFC" +
                    "\u6007\u600A\u600D\u6013\u6014\u6017\u6018\u601A" +
                    "\u601F\u6024\u602D\u6033\u6035\u6040\u6047\u6048" +
                    "\u6049\u604C\u6051\u6054\u6056\u6057\u605D\u6061" +
                    "\u6067\u6071\u607E\u607F\u6082\u6086\u6088\u608A" +
                    "\u608E\u6091\u6093\u6095\u6098\u609D\u609E\u60A2" +
                    "\u60A4\u60A5\u60A8\u60B0\u60B1\u60B7",
            "\u60BB\u60BE\u60C2\u60C4\u60C8\u60C9\u60CA\u60CB" +
                    "\u60CE\u60CF\u60D4\u60D5\u60D9\u60DB\u60DD\u60DE" +
                    "\u60E2\u60E5\u60F2\u60F5\u60F8\u60FC\u60FD\u6102" +
                    "\u6107\u610A\u610C\u6110\u6111\u6112\u6113\u6114" +
                    "\u6116\u6117\u6119\u611C\u611E\u6122\u612A\u612B" +
                    "\u6130\u6131\u6135\u6136\u6137\u6139\u6141\u6145" +
                    "\u6146\u6149\u615E\u6160\u616C\u6172\u6178\u617B" +
                    "\u617C\u617F\u6180\u6181\u6183\u6184\u618B\u618D" +
                    "\u6192\u6193\u6197\u6198\u619C\u619D\u619F\u61A0" +
                    "\u61A5\u61A8\u61AA\u61AD\u61B8\u61B9\u61BC\u61C0" +
                    "\u61C1\u61C2\u61CE\u61CF\u61D5\u61DC\u61DD\u61DE" +
                    "\u61DF\u61E1\u61E2\u61E7\u61E9\u61E5",
            "\u61EC\u61ED\u61EF\u6201\u6203\u6204\u6207\u6213" +
                    "\u6215\u621C\u6220\u6222\u6223\u6227\u6229\u622B" +
                    "\u6239\u623D\u6242\u6243\u6244\u6246\u624C\u6250" +
                    "\u6251\u6252\u6254\u6256\u625A\u625C\u6264\u626D" +
                    "\u626F\u6273\u627A\u627D\u628D\u628E\u628F\u6290" +
                    "\u62A6\u62A8\u62B3\u62B6\u62B7\u62BA\u62BE\u62BF" +
                    "\u62C4\u62CE\u62D5\u62D6\u62DA\u62EA\u62F2\u62F4" +
                    "\u62FC\u62FD\u6303\u6304\u630A\u630B\u630D\u6310" +
                    "\u6313\u6316\u6318\u6329\u632A\u632D\u6335\u6336" +
                    "\u6339\u633C\u6341\u6342\u6343\u6344\u6346\u634A" +
                    "\u634B\u634E\u6352\u6353\u6354\u6358\u635B\u6365" +
                    "\u6366\u636C\u636D\u6371\u6374\u6375",
            "\u6378\u637C\u637D\u637F\u6382\u6384\u6387\u638A" +
                    "\u6390\u6394\u6395\u6399\u639A\u639E\u63A4\u63A6" +
                    "\u63AD\u63AE\u63AF\u63BD\u63C1\u63C5\u63C8\u63CE" +
                    "\u63D1\u63D3\u63D4\u63D5\u63DC\u63E0\u63E5\u63EA" +
                    "\u63EC\u63F2\u63F3\u63F5\u63F8\u63F9\u6409\u640A" +
                    "\u6410\u6412\u6414\u6418\u641E\u6420\u6422\u6424" +
                    "\u6425\u6429\u642A\u642F\u6430\u6435\u643D\u643F" +
                    "\u644B\u644F\u6451\u6452\u6453\u6454\u645A\u645B" +
                    "\u645C\u645D\u645F\u6460\u6461\u6463\u646D\u6473" +
                    "\u6474\u647B\u647D\u6485\u6487\u648F\u6490\u6491" +
                    "\u6498\u6499\u649B\u649D\u649F\u64A1\u64A3\u64A6" +
                    "\u64A8\u64AC\u64B3\u64BD\u64BE\u64BF",
            "\u64C4\u64C9\u64CA\u64CB\u64CC\u64CE\u64D0\u64D1" +
                    "\u64D5\u64D7\u64E4\u64E5\u64E9\u64EA\u64ED\u64F0" +
                    "\u64F5\u64F7\u64FB\u64FF\u6501\u6504\u6508\u6509" +
                    "\u650A\u650F\u6513\u6514\u6516\u6519\u651B\u651E" +
                    "\u651F\u6522\u6526\u6529\u652E\u6531\u653A\u653C" +
                    "\u653D\u6543\u6547\u6549\u6550\u6552\u6554\u655F" +
                    "\u6560\u6567\u656B\u657A\u657D\u6581\u6585\u658A" +
                    "\u6592\u6595\u6598\u659D\u65A0\u65A3\u65A6\u65AE" +
                    "\u65B2\u65B3\u65B4\u65BF\u65C2\u65C8\u65C9\u65CE" +
                    "\u65D0\u65D4\u65D6\u65D8\u65DF\u65F0\u65F2\u65F4" +
                    "\u65F5\u65F9\u65FE\u65FF\u6600\u6604\u6608\u6609" +
                    "\u660D\u6611\u6612\u6615\u6616\u661D",
            "\u661E\u6621\u6622\u6623\u6624\u6626\u6629\u662A" +
                    "\u662B\u662C\u662E\u6630\u6631\u6633\u6639\u6637" +
                    "\u6640\u6645\u6646\u664A\u664C\u6651\u664E\u6657" +
                    "\u6658\u6659\u665B\u665C\u6660\u6661\u66FB\u666A" +
                    "\u666B\u666C\u667E\u6673\u6675\u667F\u6677\u6678" +
                    "\u6679\u667B\u6680\u667C\u668B\u668C\u668D\u6690" +
                    "\u6692\u6699\u669A\u669B\u669C\u669F\u66A0\u66A4" +
                    "\u66AD\u66B1\u66B2\u66B5\u66BB\u66BF\u66C0\u66C2" +
                    "\u66C3\u66C8\u66CC\u66CE\u66CF\u66D4\u66DB\u66DF" +
                    "\u66E8\u66EB\u66EC\u66EE\u66FA\u6705\u6707\u670E" +
                    "\u6713\u6719\u671C\u6720\u6722\u6733\u673E\u6745" +
                    "\u6747\u6748\u674C\u6754\u6755\u675D",
            "\u6766\u676C\u676E\u6774\u6776\u677B\u6781\u6784" +
                    "\u678E\u678F\u6791\u6793\u6796\u6798\u6799\u679B" +
                    "\u67B0\u67B1\u67B2\u67B5\u67BB\u67BC\u67BD\u67F9" +
                    "\u67C0\u67C2\u67C3\u67C5\u67C8\u67C9\u67D2\u67D7" +
                    "\u67D9\u67DC\u67E1\u67E6\u67F0\u67F2\u67F6\u67F7" +
                    "\u6852\u6814\u6819\u681D\u681F\u6828\u6827\u682C" +
                    "\u682D\u682F\u6830\u6831\u6833\u683B\u683F\u6844" +
                    "\u6845\u684A\u684C\u6855\u6857\u6858\u685B\u686B" +
                    "\u686E\u686F\u6870\u6871\u6872\u6875\u6879\u687A" +
                    "\u687B\u687C\u6882\u6884\u6886\u6888\u6896\u6898" +
                    "\u689A\u689C\u68A1\u68A3\u68A5\u68A9\u68AA\u68AE" +
                    "\u68B2\u68BB\u68C5\u68C8\u68CC\u68CF",
            "\u68D0\u68D1\u68D3\u68D6\u68D9\u68DC\u68DD\u68E5" +
                    "\u68E8\u68EA\u68EB\u68EC\u68ED\u68F0\u68F1\u68F5" +
                    "\u68F6\u68FB\u68FC\u68FD\u6906\u6909\u690A\u6910" +
                    "\u6911\u6913\u6916\u6917\u6931\u6933\u6935\u6938" +
                    "\u693B\u6942\u6945\u6949\u694E\u6957\u695B\u6963" +
                    "\u6964\u6965\u6966\u6968\u6969\u696C\u6970\u6971" +
                    "\u6972\u697A\u697B\u697F\u6980\u698D\u6992\u6996" +
                    "\u6998\u69A1\u69A5\u69A6\u69A8\u69AB\u69AD\u69AF" +
                    "\u69B7\u69B8\u69BA\u69BC\u69C5\u69C8\u69D1\u69D6" +
                    "\u69D7\u69E2\u69E5\u69EE\u69EF\u69F1\u69F3\u69F5" +
                    "\u69FE\u6A00\u6A01\u6A03\u6A0F\u6A11\u6A15\u6A1A" +
                    "\u6A1D\u6A20\u6A24\u6A28\u6A30\u6A32",
            "\u6A34\u6A37\u6A3B\u6A3E\u6A3F\u6A45\u6A46\u6A49" +
                    "\u6A4A\u6A4E\u6A50\u6A51\u6A52\u6A55\u6A56\u6A5B" +
                    "\u6A64\u6A67\u6A6A\u6A71\u6A73\u6A7E\u6A81\u6A83" +
                    "\u6A86\u6A87\u6A89\u6A8B\u6A91\u6A9B\u6A9D\u6A9E" +
                    "\u6A9F\u6AA5\u6AAB\u6AAF\u6AB0\u6AB1\u6AB4\u6ABD" +
                    "\u6ABE\u6ABF\u6AC6\u6AC9\u6AC8\u6ACC\u6AD0\u6AD4" +
                    "\u6AD5\u6AD6\u6ADC\u6ADD\u6AE4\u6AE7\u6AEC\u6AF0" +
                    "\u6AF1\u6AF2\u6AFC\u6AFD\u6B02\u6B03\u6B06\u6B07" +
                    "\u6B09\u6B0F\u6B10\u6B11\u6B17\u6B1B\u6B1E\u6B24" +
                    "\u6B28\u6B2B\u6B2C\u6B2F\u6B35\u6B36\u6B3B\u6B3F" +
                    "\u6B46\u6B4A\u6B4D\u6B52\u6B56\u6B58\u6B5D\u6B60" +
                    "\u6B67\u6B6B\u6B6E\u6B70\u6B75\u6B7D",
            "\u6B7E\u6B82\u6B85\u6B97\u6B9B\u6B9F\u6BA0\u6BA2" +
                    "\u6BA3\u6BA8\u6BA9\u6BAC\u6BAD\u6BAE\u6BB0\u6BB8" +
                    "\u6BB9\u6BBD\u6BBE\u6BC3\u6BC4\u6BC9\u6BCC\u6BD6" +
                    "\u6BDA\u6BE1\u6BE3\u6BE6\u6BE7\u6BEE\u6BF1\u6BF7" +
                    "\u6BF9\u6BFF\u6C02\u6C04\u6C05\u6C09\u6C0D\u6C0E" +
                    "\u6C10\u6C12\u6C19\u6C1F\u6C26\u6C27\u6C28\u6C2C" +
                    "\u6C2E\u6C33\u6C35\u6C36\u6C3A\u6C3B\u6C3F\u6C4A" +
                    "\u6C4B\u6C4D\u6C4F\u6C52\u6C54\u6C59\u6C5B\u6C5C" +
                    "\u6C6B\u6C6D\u6C6F\u6C74\u6C76\u6C78\u6C79\u6C7B" +
                    "\u6C85\u6C86\u6C87\u6C89\u6C94\u6C95\u6C97\u6C98" +
                    "\u6C9C\u6C9F\u6CB0\u6CB2\u6CB4\u6CC2\u6CC6\u6CCD" +
                    "\u6CCF\u6CD0\u6CD1\u6CD2\u6CD4\u6CD6",
            "\u6CDA\u6CDC\u6CE0\u6CE7\u6CE9\u6CEB\u6CEC\u6CEE" +
                    "\u6CF2\u6CF4\u6D04\u6D07\u6D0A\u6D0E\u6D0F\u6D11" +
                    "\u6D13\u6D1A\u6D26\u6D27\u6D28\u6C67\u6D2E\u6D2F" +
                    "\u6D31\u6D39\u6D3C\u6D3F\u6D57\u6D5E\u6D5F\u6D61" +
                    "\u6D65\u6D67\u6D6F\u6D70\u6D7C\u6D82\u6D87\u6D91" +
                    "\u6D92\u6D94\u6D96\u6D97\u6D98\u6DAA\u6DAC\u6DB4" +
                    "\u6DB7\u6DB9\u6DBD\u6DBF\u6DC4\u6DC8\u6DCA\u6DCE" +
                    "\u6DCF\u6DD6\u6DDB\u6DDD\u6DDF\u6DE0\u6DE2\u6DE5" +
                    "\u6DE9\u6DEF\u6DF0\u6DF4\u6DF6\u6DFC\u6E00\u6E04" +
                    "\u6E1E\u6E22\u6E27\u6E32\u6E36\u6E39\u6E3B\u6E3C" +
                    "\u6E44\u6E45\u6E48\u6E49\u6E4B\u6E4F\u6E51\u6E52" +
                    "\u6E53\u6E54\u6E57\u6E5C\u6E5D\u6E5E",
            "\u6E62\u6E63\u6E68\u6E73\u6E7B\u6E7D\u6E8D\u6E93" +
                    "\u6E99\u6EA0\u6EA7\u6EAD\u6EAE\u6EB1\u6EB3\u6EBB" +
                    "\u6EBF\u6EC0\u6EC1\u6EC3\u6EC7\u6EC8\u6ECA\u6ECD" +
                    "\u6ECE\u6ECF\u6EEB\u6EED\u6EEE\u6EF9\u6EFB\u6EFD" +
                    "\u6F04\u6F08\u6F0A\u6F0C\u6F0D\u6F16\u6F18\u6F1A" +
                    "\u6F1B\u6F26\u6F29\u6F2A\u6F2F\u6F30\u6F33\u6F36" +
                    "\u6F3B\u6F3C\u6F2D\u6F4F\u6F51\u6F52\u6F53\u6F57" +
                    "\u6F59\u6F5A\u6F5D\u6F5E\u6F61\u6F62\u6F68\u6F6C" +
                    "\u6F7D\u6F7E\u6F83\u6F87\u6F88\u6F8B\u6F8C\u6F8D" +
                    "\u6F90\u6F92\u6F93\u6F94\u6F96\u6F9A\u6F9F\u6FA0" +
                    "\u6FA5\u6FA6\u6FA7\u6FA8\u6FAE\u6FAF\u6FB0\u6FB5" +
                    "\u6FB6\u6FBC\u6FC5\u6FC7\u6FC8\u6FCA",
            "\u6FDA\u6FDE\u6FE8\u6FE9\u6FF0\u6FF5\u6FF9\u6FFC" +
                    "\u6FFD\u7000\u7005\u7006\u7007\u700D\u7017\u7020" +
                    "\u7023\u702F\u7034\u7037\u7039\u703C\u7043\u7044" +
                    "\u7048\u7049\u704A\u704B\u7054\u7055\u705D\u705E" +
                    "\u704E\u7064\u7065\u706C\u706E\u7075\u7076\u707E" +
                    "\u7081\u7085\u7086\u7094\u7095\u7096\u7097\u7098" +
                    "\u709B\u70A4\u70AB\u70B0\u70B1\u70B4\u70B7\u70CA" +
                    "\u70D1\u70D3\u70D4\u70D5\u70D6\u70D8\u70DC\u70E4" +
                    "\u70FA\u7103\u7104\u7105\u7106\u7107\u710B\u710C" +
                    "\u710F\u711E\u7120\u712B\u712D\u712F\u7130\u7131" +
                    "\u7138\u7141\u7145\u7146\u7147\u714A\u714B\u7150" +
                    "\u7152\u7157\u715A\u715C\u715E\u7160",
            "\u7168\u7179\u7180\u7185\u7187\u718C\u7192\u719A" +
                    "\u719B\u71A0\u71A2\u71AF\u71B0\u71B2\u71B3\u71BA" +
                    "\u71BF\u71C0\u71C1\u71C4\u71CB\u71CC\u71D3\u71D6" +
                    "\u71D9\u71DA\u71DC\u71F8\u71FE\u7200\u7207\u7208" +
                    "\u7209\u7213\u7217\u721A\u721D\u721F\u7224\u722B" +
                    "\u722F\u7234\u7238\u7239\u7241\u7242\u7243\u7245" +
                    "\u724E\u724F\u7250\u7253\u7255\u7256\u725A\u725C" +
                    "\u725E\u7260\u7263\u7268\u726B\u726E\u726F\u7271" +
                    "\u7277\u7278\u727B\u727C\u727F\u7284\u7289\u728D" +
                    "\u728E\u7293\u729B\u72A8\u72AD\u72AE\u72B1\u72B4" +
                    "\u72BE\u72C1\u72C7\u72C9\u72CC\u72D5\u72D6\u72D8" +
                    "\u72DF\u72E5\u72F3\u72F4\u72FA\u72FB",
            "\u72FE\u7302\u7304\u7305\u7307\u730B\u730D\u7312" +
                    "\u7313\u7318\u7319\u731E\u7322\u7324\u7327\u7328" +
                    "\u732C\u7331\u7332\u7335\u733A\u733B\u733D\u7343" +
                    "\u734D\u7350\u7352\u7356\u7358\u735D\u735E\u735F" +
                    "\u7360\u7366\u7367\u7369\u736B\u736C\u736E\u736F" +
                    "\u7371\u7377\u7379\u737C\u7380\u7381\u7383\u7385" +
                    "\u7386\u738E\u7390\u7393\u7395\u7397\u7398\u739C" +
                    "\u739E\u739F\u73A0\u73A2\u73A5\u73A6\u73AA\u73AB" +
                    "\u73AD\u73B5\u73B7\u73B9\u73BC\u73BD\u73BF\u73C5" +
                    "\u73C6\u73C9\u73CB\u73CC\u73CF\u73D2\u73D3\u73D6" +
                    "\u73D9\u73DD\u73E1\u73E3\u73E6\u73E7\u73E9\u73F4" +
                    "\u73F5\u73F7\u73F9\u73FA\u73FB\u73FD",
            "\u73FF\u7400\u7401\u7404\u7407\u740A\u7411\u741A" +
                    "\u741B\u7424\u7426\u7428\u7429\u742A\u742B\u742C" +
                    "\u742D\u742E\u742F\u7430\u7431\u7439\u7440\u7443" +
                    "\u7444\u7446\u7447\u744B\u744D\u7451\u7452\u7457" +
                    "\u745D\u7462\u7466\u7467\u7468\u746B\u746D\u746E" +
                    "\u7471\u7472\u7480\u7481\u7485\u7486\u7487\u7489" +
                    "\u748F\u7490\u7491\u7492\u7498\u7499\u749A\u749C" +
                    "\u749F\u74A0\u74A1\u74A3\u74A6\u74A8\u74A9\u74AA" +
                    "\u74AB\u74AE\u74AF\u74B1\u74B2\u74B5\u74B9\u74BB" +
                    "\u74BF\u74C8\u74C9\u74CC\u74D0\u74D3\u74D8\u74DA" +
                    "\u74DB\u74DE\u74DF\u74E4\u74E8\u74EA\u74EB\u74EF" +
                    "\u74F4\u74FA\u74FB\u74FC\u74FF\u7506",
            "\u7512\u7516\u7517\u7520\u7521\u7524\u7527\u7529" +
                    "\u752A\u752F\u7536\u7539\u753D\u753E\u753F\u7540" +
                    "\u7543\u7547\u7548\u754E\u7550\u7552\u7557\u755E" +
                    "\u755F\u7561\u756F\u7571\u7579\u757A\u757B\u757C" +
                    "\u757D\u757E\u7581\u7585\u7590\u7592\u7593\u7595" +
                    "\u7599\u759C\u75A2\u75A4\u75B4\u75BA\u75BF\u75C0" +
                    "\u75C1\u75C4\u75C6\u75CC\u75CE\u75CF\u75D7\u75DC" +
                    "\u75DF\u75E0\u75E1\u75E4\u75E7\u75EC\u75EE\u75EF" +
                    "\u75F1\u75F9\u7600\u7602\u7603\u7604\u7607\u7608" +
                    "\u760A\u760C\u760F\u7612\u7613\u7615\u7616\u7619" +
                    "\u761B\u761C\u761D\u761E\u7623\u7625\u7626\u7629" +
                    "\u762D\u7632\u7633\u7635\u7638\u7639",
            "\u763A\u763C\u764A\u7640\u7641\u7643\u7644\u7645" +
                    "\u7649\u764B\u7655\u7659\u765F\u7664\u7665\u766D" +
                    "\u766E\u766F\u7671\u7674\u7681\u7685\u768C\u768D" +
                    "\u7695\u769B\u769C\u769D\u769F\u76A0\u76A2\u76A3" +
                    "\u76A4\u76A5\u76A6\u76A7\u76A8\u76AA\u76AD\u76BD" +
                    "\u76C1\u76C5\u76C9\u76CB\u76CC\u76CE\u76D4\u76D9" +
                    "\u76E0\u76E6\u76E8\u76EC\u76F0\u76F1\u76F6\u76F9" +
                    "\u76FC\u7700\u7706\u770A\u770E\u7712\u7714\u7715" +
                    "\u7717\u7719\u771A\u771C\u7722\u7728\u772D\u772E" +
                    "\u772F\u7734\u7735\u7736\u7739\u773D\u773E\u7742" +
                    "\u7745\u7746\u774A\u774D\u774E\u774F\u7752\u7756" +
                    "\u7757\u775C\u775E\u775F\u7760\u7762",
            "\u7764\u7767\u776A\u776C\u7770\u7772\u7773\u7774" +
                    "\u777A\u777D\u7780\u7784\u778C\u778D\u7794\u7795" +
                    "\u7796\u779A\u779F\u77A2\u77A7\u77AA\u77AE\u77AF" +
                    "\u77B1\u77B5\u77BE\u77C3\u77C9\u77D1\u77D2\u77D5" +
                    "\u77D9\u77DE\u77DF\u77E0\u77E4\u77E6\u77EA\u77EC" +
                    "\u77F0\u77F1\u77F4\u77F8\u77FB\u7805\u7806\u7809" +
                    "\u780D\u780E\u7811\u781D\u7821\u7822\u7823\u782D" +
                    "\u782E\u7830\u7835\u7837\u7843\u7844\u7847\u7848" +
                    "\u784C\u784E\u7852\u785C\u785E\u7860\u7861\u7863" +
                    "\u7864\u7868\u786A\u786E\u787A\u787E\u788A\u788F" +
                    "\u7894\u7898\u78A1\u789D\u789E\u789F\u78A4\u78A8" +
                    "\u78AC\u78AD\u78B0\u78B1\u78B2\u78B3",
            "\u78BB\u78BD\u78BF\u78C7\u78C8\u78C9\u78CC\u78CE" +
                    "\u78D2\u78D3\u78D5\u78D6\u78E4\u78DB\u78DF\u78E0" +
                    "\u78E1\u78E6\u78EA\u78F2\u78F3\u7900\u78F6\u78F7" +
                    "\u78FA\u78FB\u78FF\u7906\u790C\u7910\u791A\u791C" +
                    "\u791E\u791F\u7920\u7925\u7927\u7929\u792D\u7931" +
                    "\u7934\u7935\u793B\u793D\u793F\u7944\u7945\u7946" +
                    "\u794A\u794B\u794F\u7951\u7954\u7958\u795B\u795C" +
                    "\u7967\u7969\u796B\u7972\u7979\u797B\u797C\u797E" +
                    "\u798B\u798C\u7991\u7993\u7994\u7995\u7996\u7998" +
                    "\u799B\u799C\u79A1\u79A8\u79A9\u79AB\u79AF\u79B1" +
                    "\u79B4\u79B8\u79BB\u79C2\u79C4\u79C7\u79C8\u79CA" +
                    "\u79CF\u79D4\u79D6\u79DA\u79DD\u79DE",
            "\u79E0\u79E2\u79E5\u79EA\u79EB\u79ED\u79F1\u79F8" +
                    "\u79FC\u7A02\u7A03\u7A07\u7A09\u7A0A\u7A0C\u7A11" +
                    "\u7A15\u7A1B\u7A1E\u7A21\u7A27\u7A2B\u7A2D\u7A2F" +
                    "\u7A30\u7A34\u7A35\u7A38\u7A39\u7A3A\u7A44\u7A45" +
                    "\u7A47\u7A48\u7A4C\u7A55\u7A56\u7A59\u7A5C\u7A5D" +
                    "\u7A5F\u7A60\u7A65\u7A67\u7A6A\u7A6D\u7A75\u7A78" +
                    "\u7A7E\u7A80\u7A82\u7A85\u7A86\u7A8A\u7A8B\u7A90" +
                    "\u7A91\u7A94\u7A9E\u7AA0\u7AA3\u7AAC\u7AB3\u7AB5" +
                    "\u7AB9\u7ABB\u7ABC\u7AC6\u7AC9\u7ACC\u7ACE\u7AD1" +
                    "\u7ADB\u7AE8\u7AE9\u7AEB\u7AEC\u7AF1\u7AF4\u7AFB" +
                    "\u7AFD\u7AFE\u7B07\u7B14\u7B1F\u7B23\u7B27\u7B29" +
                    "\u7B2A\u7B2B\u7B2D\u7B2E\u7B2F\u7B30",
            "\u7B31\u7B34\u7B3D\u7B3F\u7B40\u7B41\u7B47\u7B4E" +
                    "\u7B55\u7B60\u7B64\u7B66\u7B69\u7B6A\u7B6D\u7B6F" +
                    "\u7B72\u7B73\u7B77\u7B84\u7B89\u7B8E\u7B90\u7B91" +
                    "\u7B96\u7B9B\u7B9E\u7BA0\u7BA5\u7BAC\u7BAF\u7BB0" +
                    "\u7BB2\u7BB5\u7BB6\u7BBA\u7BBB\u7BBC\u7BBD\u7BC2" +
                    "\u7BC5\u7BC8\u7BCA\u7BD4\u7BD6\u7BD7\u7BD9\u7BDA" +
                    "\u7BDB\u7BE8\u7BEA\u7BF2\u7BF4\u7BF5\u7BF8\u7BF9" +
                    "\u7BFA\u7BFC\u7BFE\u7C01\u7C02\u7C03\u7C04\u7C06" +
                    "\u7C09\u7C0B\u7C0C\u7C0E\u7C0F\u7C19\u7C1B\u7C20" +
                    "\u7C25\u7C26\u7C28\u7C2C\u7C31\u7C33\u7C34\u7C36" +
                    "\u7C39\u7C3A\u7C46\u7C4A\u7C55\u7C51\u7C52\u7C53" +
                    "\u7C59\u7C5A\u7C5B\u7C5C\u7C5D\u7C5E",
            "\u7C61\u7C63\u7C67\u7C69\u7C6D\u7C6E\u7C70\u7C72" +
                    "\u7C79\u7C7C\u7C7D\u7C86\u7C87\u7C8F\u7C94\u7C9E" +
                    "\u7CA0\u7CA6\u7CB0\u7CB6\u7CB7\u7CBA\u7CBB\u7CBC" +
                    "\u7CBF\u7CC4\u7CC7\u7CC8\u7CC9\u7CCD\u7CCF\u7CD3" +
                    "\u7CD4\u7CD5\u7CD7\u7CD9\u7CDA\u7CDD\u7CE6\u7CE9" +
                    "\u7CEB\u7CF5\u7D03\u7D07\u7D08\u7D09\u7D0F\u7D11" +
                    "\u7D12\u7D13\u7D16\u7D1D\u7D1E\u7D23\u7D26\u7D2A" +
                    "\u7D2D\u7D31\u7D3C\u7D3D\u7D3E\u7D40\u7D41\u7D47" +
                    "\u7D48\u7D4D\u7D51\u7D53\u7D57\u7D59\u7D5A\u7D5C" +
                    "\u7D5D\u7D65\u7D67\u7D6A\u7D70\u7D78\u7D7A\u7D7B" +
                    "\u7D7F\u7D81\u7D82\u7D83\u7D85\u7D86\u7D88\u7D8B" +
                    "\u7D8C\u7D8D\u7D91\u7D96\u7D97\u7D9D",
            "\u7D9E\u7DA6\u7DA7\u7DAA\u7DB3\u7DB6\u7DB7\u7DB9" +
                    "\u7DC2\u7DC3\u7DC4\u7DC5\u7DC6\u7DCC\u7DCD\u7DCE" +
                    "\u7DD7\u7DD9\u7E00\u7DE2\u7DE5\u7DE6\u7DEA\u7DEB" +
                    "\u7DED\u7DF1\u7DF5\u7DF6\u7DF9\u7DFA\u7E08\u7E10" +
                    "\u7E11\u7E15\u7E17\u7E1C\u7E1D\u7E20\u7E27\u7E28" +
                    "\u7E2C\u7E2D\u7E2F\u7E33\u7E36\u7E3F\u7E44\u7E45" +
                    "\u7E47\u7E4E\u7E50\u7E52\u7E58\u7E5F\u7E61\u7E62" +
                    "\u7E65\u7E6B\u7E6E\u7E6F\u7E73\u7E78\u7E7E\u7E81" +
                    "\u7E86\u7E87\u7E8A\u7E8D\u7E91\u7E95\u7E98\u7E9A" +
                    "\u7E9D\u7E9E\u7F3C\u7F3B\u7F3D\u7F3E\u7F3F\u7F43" +
                    "\u7F44\u7F47\u7F4F\u7F52\u7F53\u7F5B\u7F5C\u7F5D" +
                    "\u7F61\u7F63\u7F64\u7F65\u7F66\u7F6D",
            "\u7F71\u7F7D\u7F7E\u7F7F\u7F80\u7F8B\u7F8D\u7F8F" +
                    "\u7F90\u7F91\u7F96\u7F97\u7F9C\u7FA1\u7FA2\u7FA6" +
                    "\u7FAA\u7FAD\u7FB4\u7FBC\u7FBF\u7FC0\u7FC3\u7FC8" +
                    "\u7FCE\u7FCF\u7FDB\u7FDF\u7FE3\u7FE5\u7FE8\u7FEC" +
                    "\u7FEE\u7FEF\u7FF2\u7FFA\u7FFD\u7FFE\u7FFF\u8007" +
                    "\u8008\u800A\u800D\u800E\u800F\u8011\u8013\u8014" +
                    "\u8016\u801D\u801E\u801F\u8020\u8024\u8026\u802C" +
                    "\u802E\u8030\u8034\u8035\u8037\u8039\u803A\u803C" +
                    "\u803E\u8040\u8044\u8060\u8064\u8066\u806D\u8071" +
                    "\u8075\u8081\u8088\u808E\u809C\u809E\u80A6\u80A7" +
                    "\u80AB\u80B8\u80B9\u80C8\u80CD\u80CF\u80D2\u80D4" +
                    "\u80D5\u80D7\u80D8\u80E0\u80ED\u80EE",
            "\u80F0\u80F2\u80F3\u80F6\u80F9\u80FA\u80FE\u8103" +
                    "\u810B\u8116\u8117\u8118\u811C\u811E\u8120\u8124" +
                    "\u8127\u812C\u8130\u8135\u813A\u813C\u8145\u8147" +
                    "\u814A\u814C\u8152\u8157\u8160\u8161\u8167\u8168" +
                    "\u8169\u816D\u816F\u8177\u8181\u8190\u8184\u8185" +
                    "\u8186\u818B\u818E\u8196\u8198\u819B\u819E\u81A2" +
                    "\u81AE\u81B2\u81B4\u81BB\u81CB\u81C3\u81C5\u81CA" +
                    "\u81CE\u81CF\u81D5\u81D7\u81DB\u81DD\u81DE\u81E1" +
                    "\u81E4\u81EB\u81EC\u81F0\u81F1\u81F2\u81F5\u81F6" +
                    "\u81F8\u81F9\u81FD\u81FF\u8200\u8203\u820F\u8213" +
                    "\u8214\u8219\u821A\u821D\u8221\u8222\u8228\u8232" +
                    "\u8234\u823A\u8243\u8244\u8245\u8246",
            "\u824B\u824E\u824F\u8251\u8256\u825C\u8260\u8263" +
                    "\u8267\u826D\u8274\u827B\u827D\u827F\u8280\u8281" +
                    "\u8283\u8284\u8287\u8289\u828A\u828E\u8291\u8294" +
                    "\u8296\u8298\u829A\u829B\u82A0\u82A1\u82A3\u82A4" +
                    "\u82A7\u82A8\u82A9\u82AA\u82AE\u82B0\u82B2\u82B4" +
                    "\u82B7\u82BA\u82BC\u82BE\u82BF\u82C6\u82D0\u82D5" +
                    "\u82DA\u82E0\u82E2\u82E4\u82E8\u82EA\u82ED\u82EF" +
                    "\u82F6\u82F7\u82FD\u82FE\u8300\u8301\u8307\u8308" +
                    "\u830A\u830B\u8354\u831B\u831D\u831E\u831F\u8321" +
                    "\u8322\u832C\u832D\u832E\u8330\u8333\u8337\u833A" +
                    "\u833C\u833D\u8342\u8343\u8344\u8347\u834D\u834E" +
                    "\u8351\u8355\u8356\u8357\u8370\u8378",
            "\u837D\u837F\u8380\u8382\u8384\u8386\u838D\u8392" +
                    "\u8394\u8395\u8398\u8399\u839B\u839C\u839D\u83A6" +
                    "\u83A7\u83A9\u83AC\u83BE\u83BF\u83C0\u83C7\u83C9" +
                    "\u83CF\u83D0\u83D1\u83D4\u83DD\u8353\u83E8\u83EA" +
                    "\u83F6\u83F8\u83F9\u83FC\u8401\u8406\u840A\u840F" +
                    "\u8411\u8415\u8419\u83AD\u842F\u8439\u8445\u8447" +
                    "\u8448\u844A\u844D\u844F\u8451\u8452\u8456\u8458" +
                    "\u8459\u845A\u845C\u8460\u8464\u8465\u8467\u846A" +
                    "\u8470\u8473\u8474\u8476\u8478\u847C\u847D\u8481" +
                    "\u8485\u8492\u8493\u8495\u849E\u84A6\u84A8\u84A9" +
                    "\u84AA\u84AF\u84B1\u84B4\u84BA\u84BD\u84BE\u84C0" +
                    "\u84C2\u84C7\u84C8\u84CC\u84CF\u84D3",
            "\u84DC\u84E7\u84EA\u84EF\u84F0\u84F1\u84F2\u84F7" +
                    "\u8532\u84FA\u84FB\u84FD\u8502\u8503\u8507\u850C" +
                    "\u850E\u8510\u851C\u851E\u8522\u8523\u8524\u8525" +
                    "\u8527\u852A\u852B\u852F\u8533\u8534\u8536\u853F" +
                    "\u8546\u854F\u8550\u8551\u8552\u8553\u8556\u8559" +
                    "\u855C\u855D\u855E\u855F\u8560\u8561\u8562\u8564" +
                    "\u856B\u856F\u8579\u857A\u857B\u857D\u857F\u8581" +
                    "\u8585\u8586\u8589\u858B\u858C\u858F\u8593\u8598" +
                    "\u859D\u859F\u85A0\u85A2\u85A5\u85A7\u85B4\u85B6" +
                    "\u85B7\u85B8\u85BC\u85BD\u85BE\u85BF\u85C2\u85C7" +
                    "\u85CA\u85CB\u85CE\u85AD\u85D8\u85DA\u85DF\u85E0" +
                    "\u85E6\u85E8\u85ED\u85F3\u85F6\u85FC",
            "\u85FF\u8600\u8604\u8605\u860D\u860E\u8610\u8611" +
                    "\u8612\u8618\u8619\u861B\u861E\u8621\u8627\u8629" +
                    "\u8636\u8638\u863A\u863C\u863D\u8640\u8642\u8646" +
                    "\u8652\u8653\u8656\u8657\u8658\u8659\u865D\u8660" +
                    "\u8661\u8662\u8663\u8664\u8669\u866C\u866F\u8675" +
                    "\u8676\u8677\u867A\u868D\u8691\u8696\u8698\u869A" +
                    "\u869C\u86A1\u86A6\u86A7\u86A8\u86AD\u86B1\u86B3" +
                    "\u86B4\u86B5\u86B7\u86B8\u86B9\u86BF\u86C0\u86C1" +
                    "\u86C3\u86C5\u86D1\u86D2\u86D5\u86D7\u86DA\u86DC" +
                    "\u86E0\u86E3\u86E5\u86E7\u8688\u86FA\u86FC\u86FD" +
                    "\u8704\u8705\u8707\u870B\u870E\u870F\u8710\u8713" +
                    "\u8714\u8719\u871E\u871F\u8721\u8723",
            "\u8728\u872E\u872F\u8731\u8732\u8739\u873A\u873C" +
                    "\u873D\u873E\u8740\u8743\u8745\u874D\u8758\u875D" +
                    "\u8761\u8764\u8765\u876F\u8771\u8772\u877B\u8783" +
                    "\u8784\u8785\u8786\u8787\u8788\u8789\u878B\u878C" +
                    "\u8790\u8793\u8795\u8797\u8798\u8799\u879E\u87A0" +
                    "\u87A3\u87A7\u87AC\u87AD\u87AE\u87B1\u87B5\u87BE" +
                    "\u87BF\u87C1\u87C8\u87C9\u87CA\u87CE\u87D5\u87D6" +
                    "\u87D9\u87DA\u87DC\u87DF\u87E2\u87E3\u87E4\u87EA" +
                    "\u87EB\u87ED\u87F1\u87F3\u87F8\u87FA\u87FF\u8801" +
                    "\u8803\u8806\u8809\u880A\u880B\u8810\u8819\u8812" +
                    "\u8813\u8814\u8818\u881A\u881B\u881C\u881E\u881F" +
                    "\u8828\u882D\u882E\u8830\u8832\u8835",
            "\u883A\u883C\u8841\u8843\u8845\u8848\u8849\u884A" +
                    "\u884B\u884E\u8851\u8855\u8856\u8858\u885A\u885C" +
                    "\u885F\u8860\u8864\u8869\u8871\u8879\u887B\u8880" +
                    "\u8898\u889A\u889B\u889C\u889F\u88A0\u88A8\u88AA" +
                    "\u88BA\u88BD\u88BE\u88C0\u88CA\u88CB\u88CC\u88CD" +
                    "\u88CE\u88D1\u88D2\u88D3\u88DB\u88DE\u88E7\u88EF" +
                    "\u88F0\u88F1\u88F5\u88F7\u8901\u8906\u890D\u890E" +
                    "\u890F\u8915\u8916\u8918\u8919\u891A\u891C\u8920" +
                    "\u8926\u8927\u8928\u8930\u8931\u8932\u8935\u8939" +
                    "\u893A\u893E\u8940\u8942\u8945\u8946\u8949\u894F" +
                    "\u8952\u8957\u895A\u895B\u895C\u8961\u8962\u8963" +
                    "\u896B\u896E\u8970\u8973\u8975\u897A",
            "\u897B\u897C\u897D\u8989\u898D\u8990\u8994\u8995" +
                    "\u899B\u899C\u899F\u89A0\u89A5\u89B0\u89B4\u89B5" +
                    "\u89B6\u89B7\u89BC\u89D4\u89D5\u89D6\u89D7\u89D8" +
                    "\u89E5\u89E9\u89EB\u89ED\u89F1\u89F3\u89F6\u89F9" +
                    "\u89FD\u89FF\u8A04\u8A05\u8A07\u8A0F\u8A11\u8A12" +
                    "\u8A14\u8A15\u8A1E\u8A20\u8A22\u8A24\u8A26\u8A2B" +
                    "\u8A2C\u8A2F\u8A35\u8A37\u8A3D\u8A3E\u8A40\u8A43" +
                    "\u8A45\u8A47\u8A49\u8A4D\u8A4E\u8A53\u8A56\u8A57" +
                    "\u8A58\u8A5C\u8A5D\u8A61\u8A65\u8A67\u8A75\u8A76" +
                    "\u8A77\u8A79\u8A7A\u8A7B\u8A7E\u8A7F\u8A80\u8A83" +
                    "\u8A86\u8A8B\u8A8F\u8A90\u8A92\u8A96\u8A97\u8A99" +
                    "\u8A9F\u8AA7\u8AA9\u8AAE\u8AAF\u8AB3",
            "\u8AB6\u8AB7\u8ABB\u8ABE\u8AC3\u8AC6\u8AC8\u8AC9" +
                    "\u8ACA\u8AD1\u8AD3\u8AD4\u8AD5\u8AD7\u8ADD\u8ADF" +
                    "\u8AEC\u8AF0\u8AF4\u8AF5\u8AF6\u8AFC\u8AFF\u8B05" +
                    "\u8B06\u8B0B\u8B11\u8B1C\u8B1E\u8B1F\u8B0A\u8B2D" +
                    "\u8B30\u8B37\u8B3C\u8B42\u8B43\u8B44\u8B45\u8B46" +
                    "\u8B48\u8B52\u8B53\u8B54\u8B59\u8B4D\u8B5E\u8B63" +
                    "\u8B6D\u8B76\u8B78\u8B79\u8B7C\u8B7E\u8B81\u8B84" +
                    "\u8B85\u8B8B\u8B8D\u8B8F\u8B94\u8B95\u8B9C\u8B9E" +
                    "\u8B9F\u8C38\u8C39\u8C3D\u8C3E\u8C45\u8C47\u8C49" +
                    "\u8C4B\u8C4F\u8C51\u8C53\u8C54\u8C57\u8C58\u8C5B" +
                    "\u8C5D\u8C59\u8C63\u8C64\u8C66\u8C68\u8C69\u8C6D" +
                    "\u8C73\u8C75\u8C76\u8C7B\u8C7E\u8C86",
            "\u8C87\u8C8B\u8C90\u8C92\u8C93\u8C99\u8C9B\u8C9C" +
                    "\u8CA4\u8CB9\u8CBA\u8CC5\u8CC6\u8CC9\u8CCB\u8CCF" +
                    "\u8CD6\u8CD5\u8CD9\u8CDD\u8CE1\u8CE8\u8CEC\u8CEF" +
                    "\u8CF0\u8CF2\u8CF5\u8CF7\u8CF8\u8CFE\u8CFF\u8D01" +
                    "\u8D03\u8D09\u8D12\u8D17\u8D1B\u8D65\u8D69\u8D6C" +
                    "\u8D6E\u8D7F\u8D82\u8D84\u8D88\u8D8D\u8D90\u8D91" +
                    "\u8D95\u8D9E\u8D9F\u8DA0\u8DA6\u8DAB\u8DAC\u8DAF" +
                    "\u8DB2\u8DB5\u8DB7\u8DB9\u8DBB\u8DC0\u8DC5\u8DC6" +
                    "\u8DC7\u8DC8\u8DCA\u8DCE\u8DD1\u8DD4\u8DD5\u8DD7" +
                    "\u8DD9\u8DE4\u8DE5\u8DE7\u8DEC\u8DF0\u8DBC\u8DF1" +
                    "\u8DF2\u8DF4\u8DFD\u8E01\u8E04\u8E05\u8E06\u8E0B" +
                    "\u8E11\u8E14\u8E16\u8E20\u8E21\u8E22",
            "\u8E23\u8E26\u8E27\u8E31\u8E33\u8E36\u8E37\u8E38" +
                    "\u8E39\u8E3D\u8E40\u8E41\u8E4B\u8E4D\u8E4E\u8E4F" +
                    "\u8E54\u8E5B\u8E5C\u8E5D\u8E5E\u8E61\u8E62\u8E69" +
                    "\u8E6C\u8E6D\u8E6F\u8E70\u8E71\u8E79\u8E7A\u8E7B" +
                    "\u8E82\u8E83\u8E89\u8E90\u8E92\u8E95\u8E9A\u8E9B" +
                    "\u8E9D\u8E9E\u8EA2\u8EA7\u8EA9\u8EAD\u8EAE\u8EB3" +
                    "\u8EB5\u8EBA\u8EBB\u8EC0\u8EC1\u8EC3\u8EC4\u8EC7" +
                    "\u8ECF\u8ED1\u8ED4\u8EDC\u8EE8\u8EEE\u8EF0\u8EF1" +
                    "\u8EF7\u8EF9\u8EFA\u8EED\u8F00\u8F02\u8F07\u8F08" +
                    "\u8F0F\u8F10\u8F16\u8F17\u8F18\u8F1E\u8F20\u8F21" +
                    "\u8F23\u8F25\u8F27\u8F28\u8F2C\u8F2D\u8F2E\u8F34" +
                    "\u8F35\u8F36\u8F37\u8F3A\u8F40\u8F41",
            "\u8F43\u8F47\u8F4F\u8F51\u8F52\u8F53\u8F54\u8F55" +
                    "\u8F58\u8F5D\u8F5E\u8F65\u8F9D\u8FA0\u8FA1\u8FA4" +
                    "\u8FA5\u8FA6\u8FB5\u8FB6\u8FB8\u8FBE\u8FC0\u8FC1" +
                    "\u8FC6\u8FCA\u8FCB\u8FCD\u8FD0\u8FD2\u8FD3\u8FD5" +
                    "\u8FE0\u8FE3\u8FE4\u8FE8\u8FEE\u8FF1\u8FF5\u8FF6" +
                    "\u8FFB\u8FFE\u9002\u9004\u9008\u900C\u9018\u901B" +
                    "\u9028\u9029\u902F\u902A\u902C\u902D\u9033\u9034" +
                    "\u9037\u903F\u9043\u9044\u904C\u905B\u905D\u9062" +
                    "\u9066\u9067\u906C\u9070\u9074\u9079\u9085\u9088" +
                    "\u908B\u908C\u908E\u9090\u9095\u9097\u9098\u9099" +
                    "\u909B\u90A0\u90A1\u90A2\u90A5\u90B0\u90B2\u90B3" +
                    "\u90B4\u90B6\u90BD\u90CC\u90BE\u90C3",
            "\u90C4\u90C5\u90C7\u90C8\u90D5\u90D7\u90D8\u90D9" +
                    "\u90DC\u90DD\u90DF\u90E5\u90D2\u90F6\u90EB\u90EF" +
                    "\u90F0\u90F4\u90FE\u90FF\u9100\u9104\u9105\u9106" +
                    "\u9108\u910D\u9110\u9114\u9116\u9117\u9118\u911A" +
                    "\u911C\u911E\u9120\u9125\u9122\u9123\u9127\u9129" +
                    "\u912E\u912F\u9131\u9134\u9136\u9137\u9139\u913A" +
                    "\u913C\u913D\u9143\u9147\u9148\u914F\u9153\u9157" +
                    "\u9159\u915A\u915B\u9161\u9164\u9167\u916D\u9174" +
                    "\u9179\u917A\u917B\u9181\u9183\u9185\u9186\u918A" +
                    "\u918E\u9191\u9193\u9194\u9195\u9198\u919E\u91A1" +
                    "\u91A6\u91A8\u91AC\u91AD\u91AE\u91B0\u91B1\u91B2" +
                    "\u91B3\u91B6\u91BB\u91BC\u91BD\u91BF",
            "\u91C2\u91C3\u91C5\u91D3\u91D4\u91D7\u91D9\u91DA" +
                    "\u91DE\u91E4\u91E5\u91E9\u91EA\u91EC\u91ED\u91EE" +
                    "\u91EF\u91F0\u91F1\u91F7\u91F9\u91FB\u91FD\u9200" +
                    "\u9201\u9204\u9205\u9206\u9207\u9209\u920A\u920C" +
                    "\u9210\u9212\u9213\u9216\u9218\u921C\u921D\u9223" +
                    "\u9224\u9225\u9226\u9228\u922E\u922F\u9230\u9233" +
                    "\u9235\u9236\u9238\u9239\u923A\u923C\u923E\u9240" +
                    "\u9242\u9243\u9246\u9247\u924A\u924D\u924E\u924F" +
                    "\u9251\u9258\u9259\u925C\u925D\u9260\u9261\u9265" +
                    "\u9267\u9268\u9269\u926E\u926F\u9270\u9275\u9276" +
                    "\u9277\u9278\u9279\u927B\u927C\u927D\u927F\u9288" +
                    "\u9289\u928A\u928D\u928E\u9292\u9297",
            "\u9299\u929F\u92A0\u92A4\u92A5\u92A7\u92A8\u92AB" +
                    "\u92AF\u92B2\u92B6\u92B8\u92BA\u92BB\u92BC\u92BD" +
                    "\u92BF\u92C0\u92C1\u92C2\u92C3\u92C5\u92C6\u92C7" +
                    "\u92C8\u92CB\u92CC\u92CD\u92CE\u92D0\u92D3\u92D5" +
                    "\u92D7\u92D8\u92D9\u92DC\u92DD\u92DF\u92E0\u92E1" +
                    "\u92E3\u92E5\u92E7\u92E8\u92EC\u92EE\u92F0\u92F9" +
                    "\u92FB\u92FF\u9300\u9302\u9308\u930D\u9311\u9314" +
                    "\u9315\u931C\u931D\u931E\u931F\u9321\u9324\u9325" +
                    "\u9327\u9329\u932A\u9333\u9334\u9336\u9337\u9347" +
                    "\u9348\u9349\u9350\u9351\u9352\u9355\u9357\u9358" +
                    "\u935A\u935E\u9364\u9365\u9367\u9369\u936A\u936D" +
                    "\u936F\u9370\u9371\u9373\u9374\u9376",
            "\u937A\u937D\u937F\u9380\u9381\u9382\u9388\u938A" +
                    "\u938B\u938D\u938F\u9392\u9395\u9398\u939B\u939E" +
                    "\u93A1\u93A3\u93A4\u93A6\u93A8\u93AB\u93B4\u93B5" +
                    "\u93B6\u93BA\u93A9\u93C1\u93C4\u93C5\u93C6\u93C7" +
                    "\u93C9\u93CA\u93CB\u93CC\u93CD\u93D3\u93D9\u93DC" +
                    "\u93DE\u93DF\u93E2\u93E6\u93E7\u93F9\u93F7\u93F8" +
                    "\u93FA\u93FB\u93FD\u9401\u9402\u9404\u9408\u9409" +
                    "\u940D\u940E\u940F\u9415\u9416\u9417\u941F\u942E" +
                    "\u942F\u9431\u9432\u9433\u9434\u943B\u943F\u943D" +
                    "\u9443\u9445\u9448\u944A\u944C\u9455\u9459\u945C" +
                    "\u945F\u9461\u9463\u9468\u946B\u946D\u946E\u946F" +
                    "\u9471\u9472\u9484\u9483\u9578\u9579",
            "\u957E\u9584\u9588\u958C\u958D\u958E\u959D\u959E" +
                    "\u959F\u95A1\u95A6\u95A9\u95AB\u95AC\u95B4\u95B6" +
                    "\u95BA\u95BD\u95BF\u95C6\u95C8\u95C9\u95CB\u95D0" +
                    "\u95D1\u95D2\u95D3\u95D9\u95DA\u95DD\u95DE\u95DF" +
                    "\u95E0\u95E4\u95E6\u961D\u961E\u9622\u9624\u9625" +
                    "\u9626\u962C\u9631\u9633\u9637\u9638\u9639\u963A" +
                    "\u963C\u963D\u9641\u9652\u9654\u9656\u9657\u9658" +
                    "\u9661\u966E\u9674\u967B\u967C\u967E\u967F\u9681" +
                    "\u9682\u9683\u9684\u9689\u9691\u9696\u969A\u969D" +
                    "\u969F\u96A4\u96A5\u96A6\u96A9\u96AE\u96AF\u96B3" +
                    "\u96BA\u96CA\u96D2\u5DB2\u96D8\u96DA\u96DD\u96DE" +
                    "\u96DF\u96E9\u96EF\u96F1\u96FA\u9702",
            "\u9703\u9705\u9709\u971A\u971B\u971D\u9721\u9722" +
                    "\u9723\u9728\u9731\u9733\u9741\u9743\u974A\u974E" +
                    "\u974F\u9755\u9757\u9758\u975A\u975B\u9763\u9767" +
                    "\u976A\u976E\u9773\u9776\u9777\u9778\u977B\u977D" +
                    "\u977F\u9780\u9789\u9795\u9796\u9797\u9799\u979A" +
                    "\u979E\u979F\u97A2\u97AC\u97AE\u97B1\u97B2\u97B5" +
                    "\u97B6\u97B8\u97B9\u97BA\u97BC\u97BE\u97BF\u97C1" +
                    "\u97C4\u97C5\u97C7\u97C9\u97CA\u97CC\u97CD\u97CE" +
                    "\u97D0\u97D1\u97D4\u97D7\u97D8\u97D9\u97DD\u97DE" +
                    "\u97E0\u97DB\u97E1\u97E4\u97EF\u97F1\u97F4\u97F7" +
                    "\u97F8\u97FA\u9807\u980A\u9819\u980D\u980E\u9814" +
                    "\u9816\u981C\u981E\u9820\u9823\u9826",
            "\u982B\u982E\u982F\u9830\u9832\u9833\u9835\u9825" +
                    "\u983E\u9844\u9847\u984A\u9851\u9852\u9853\u9856" +
                    "\u9857\u9859\u985A\u9862\u9863\u9865\u9866\u986A" +
                    "\u986C\u98AB\u98AD\u98AE\u98B0\u98B4\u98B7\u98B8" +
                    "\u98BA\u98BB\u98BF\u98C2\u98C5\u98C8\u98CC\u98E1" +
                    "\u98E3\u98E5\u98E6\u98E7\u98EA\u98F3\u98F6\u9902" +
                    "\u9907\u9908\u9911\u9915\u9916\u9917\u991A\u991B" +
                    "\u991C\u991F\u9922\u9926\u9927\u992B\u9931\u9932" +
                    "\u9933\u9934\u9935\u9939\u993A\u993B\u993C\u9940" +
                    "\u9941\u9946\u9947\u9948\u994D\u994E\u9954\u9958" +
                    "\u9959\u995B\u995C\u995E\u995F\u9960\u999B\u999D" +
                    "\u999F\u99A6\u99B0\u99B1\u99B2\u99B5",
            "\u99B9\u99BA\u99BD\u99BF\u99C3\u99C9\u99D3\u99D4" +
                    "\u99D9\u99DA\u99DC\u99DE\u99E7\u99EA\u99EB\u99EC" +
                    "\u99F0\u99F4\u99F5\u99F9\u99FD\u99FE\u9A02\u9A03" +
                    "\u9A04\u9A0B\u9A0C\u9A10\u9A11\u9A16\u9A1E\u9A20" +
                    "\u9A22\u9A23\u9A24\u9A27\u9A2D\u9A2E\u9A33\u9A35" +
                    "\u9A36\u9A38\u9A47\u9A41\u9A44\u9A4A\u9A4B\u9A4C" +
                    "\u9A4E\u9A51\u9A54\u9A56\u9A5D\u9AAA\u9AAC\u9AAE" +
                    "\u9AAF\u9AB2\u9AB4\u9AB5\u9AB6\u9AB9\u9ABB\u9ABE" +
                    "\u9ABF\u9AC1\u9AC3\u9AC6\u9AC8\u9ACE\u9AD0\u9AD2" +
                    "\u9AD5\u9AD6\u9AD7\u9ADB\u9ADC\u9AE0\u9AE4\u9AE5" +
                    "\u9AE7\u9AE9\u9AEC\u9AF2\u9AF3\u9AF5\u9AF9\u9AFA" +
                    "\u9AFD\u9AFF\u9B00\u9B01\u9B02\u9B03",
            "\u9B04\u9B05\u9B08\u9B09\u9B0B\u9B0C\u9B0D\u9B0E" +
                    "\u9B10\u9B12\u9B16\u9B19\u9B1B\u9B1C\u9B20\u9B26" +
                    "\u9B2B\u9B2D\u9B33\u9B34\u9B35\u9B37\u9B39\u9B3A" +
                    "\u9B3D\u9B48\u9B4B\u9B4C\u9B55\u9B56\u9B57\u9B5B" +
                    "\u9B5E\u9B61\u9B63\u9B65\u9B66\u9B68\u9B6A\u9B6B" +
                    "\u9B6C\u9B6D\u9B6E\u9B73\u9B75\u9B77\u9B78\u9B79" +
                    "\u9B7F\u9B80\u9B84\u9B85\u9B86\u9B87\u9B89\u9B8A" +
                    "\u9B8B\u9B8D\u9B8F\u9B90\u9B94\u9B9A\u9B9D\u9B9E" +
                    "\u9BA6\u9BA7\u9BA9\u9BAC\u9BB0\u9BB1\u9BB2\u9BB7" +
                    "\u9BB8\u9BBB\u9BBC\u9BBE\u9BBF\u9BC1\u9BC7\u9BC8" +
                    "\u9BCE\u9BD0\u9BD7\u9BD8\u9BDD\u9BDF\u9BE5\u9BE7" +
                    "\u9BEA\u9BEB\u9BEF\u9BF3\u9BF7\u9BF8",
            "\u9BF9\u9BFA\u9BFD\u9BFF\u9C00\u9C02\u9C0B\u9C0F" +
                    "\u9C11\u9C16\u9C18\u9C19\u9C1A\u9C1C\u9C1E\u9C22" +
                    "\u9C23\u9C26\u9C27\u9C28\u9C29\u9C2A\u9C31\u9C35" +
                    "\u9C36\u9C37\u9C3D\u9C41\u9C43\u9C44\u9C45\u9C49" +
                    "\u9C4A\u9C4E\u9C4F\u9C50\u9C53\u9C54\u9C56\u9C58" +
                    "\u9C5B\u9C5D\u9C5E\u9C5F\u9C63\u9C69\u9C6A\u9C5C" +
                    "\u9C6B\u9C68\u9C6E\u9C70\u9C72\u9C75\u9C77\u9C7B" +
                    "\u9CE6\u9CF2\u9CF7\u9CF9\u9D0B\u9D02\u9D11\u9D17" +
                    "\u9D18\u9D1C\u9D1D\u9D1E\u9D2F\u9D30\u9D32\u9D33" +
                    "\u9D34\u9D3A\u9D3C\u9D45\u9D3D\u9D42\u9D43\u9D47" +
                    "\u9D4A\u9D53\u9D54\u9D5F\u9D63\u9D62\u9D65\u9D69" +
                    "\u9D6A\u9D6B\u9D70\u9D76\u9D77\u9D7B",
            "\u9D7C\u9D7E\u9D83\u9D84\u9D86\u9D8A\u9D8D\u9D8E" +
                    "\u9D92\u9D93\u9D95\u9D96\u9D97\u9D98\u9DA1\u9DAA" +
                    "\u9DAC\u9DAE\u9DB1\u9DB5\u9DB9\u9DBC\u9DBF\u9DC3" +
                    "\u9DC7\u9DC9\u9DCA\u9DD4\u9DD5\u9DD6\u9DD7\u9DDA" +
                    "\u9DDE\u9DDF\u9DE0\u9DE5\u9DE7\u9DE9\u9DEB\u9DEE" +
                    "\u9DF0\u9DF3\u9DF4\u9DFE\u9E0A\u9E02\u9E07\u9E0E" +
                    "\u9E10\u9E11\u9E12\u9E15\u9E16\u9E19\u9E1C\u9E1D" +
                    "\u9E7A\u9E7B\u9E7C\u9E80\u9E82\u9E83\u9E84\u9E85" +
                    "\u9E87\u9E8E\u9E8F\u9E96\u9E98\u9E9B\u9E9E\u9EA4" +
                    "\u9EA8\u9EAC\u9EAE\u9EAF\u9EB0\u9EB3\u9EB4\u9EB5" +
                    "\u9EC6\u9EC8\u9ECB\u9ED5\u9EDF\u9EE4\u9EE7\u9EEC" +
                    "\u9EED\u9EEE\u9EF0\u9EF1\u9EF2\u9EF5",
            "\u9EF8\u9EFF\u9F02\u9F03\u9F09\u9F0F\u9F10\u9F11" +
                    "\u9F12\u9F14\u9F16\u9F17\u9F19\u9F1A\u9F1B\u9F1F" +
                    "\u9F22\u9F26\u9F2A\u9F2B\u9F2F\u9F31\u9F32\u9F34" +
                    "\u9F37\u9F39\u9F3A\u9F3C\u9F3D\u9F3F\u9F41\u9F43" +
                    "\u9F44\u9F45\u9F46\u9F47\u9F53\u9F55\u9F56\u9F57" +
                    "\u9F58\u9F5A\u9F5D\u9F5E\u9F68\u9F69\u9F6D\u9F6E" +
                    "\u9F6F\u9F70\u9F71\u9F73\u9F75\u9F7A\u9F7D\u9F8F" +
                    "\u9F90\u9F91\u9F92\u9F94\u9F96\u9F97\u9F9E\u9FA1" +
                    "\u9FA2\u9FA3\u9FA5\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +
                    "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )

        val b2c: Array<CharArray?> = arrayOfNulls<CharArray>(b2cStr.size)
        val b2cSB: CharArray?

        init {
            for (i in b2cStr.indices) {
                if (b2cStr[i] == null) b2c[i] = DoubleByte.B2C_UNMAPPABLE
                else b2c[i] = b2cStr[i]!!.toCharArray()
            }
            b2cSB = b2cSBStr.toCharArray()
        }
    }

    object EncodeHolder {
        val c2b: CharArray = CharArray(0x5a00)
        val c2bIndex: CharArray = CharArray(0x100)

        init {
            val b2cNR: String? = null
            val c2bNR: String? = null
            DoubleByte.Encoder.initC2B(
                DecodeHolder.b2cStr, DecodeHolder.b2cSBStr,
                b2cNR, c2bNR,
                0x21, 0x7e,
                c2b, c2bIndex
            )
        }
    }
}
