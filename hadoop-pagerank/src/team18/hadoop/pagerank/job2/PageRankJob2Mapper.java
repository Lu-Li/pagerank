/**
 * Copyright (c) 2014 Daniele Pantaleone <danielepantaleone@icloud.com>
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
 * 
 * @author Daniele Pantaleone
 * @version 1.0
 * @copyright Daniele Pantaleone, 17 October, 2014
 * @package it.uniroma1.hadoop.pagerank.job2
 */

package team18.hadoop.pagerank.job2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import team18.hadoop.pagerank.PageRank;

import java.io.IOException;

public class PageRankJob2Mapper extends Mapper<LongWritable, Text, Text, Text> {
    
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        
        /* PageRank calculation algorithm (mapper)
         * Input file format (separator is TAB):
         * 
         *     <title>    <page-rank>    <link1>,<link2>,<link3>,<link4>,... ,<linkN>
         * 
         * Output has 2 kind of records:
         * One record composed by the collection of links of each page:
         *     
         *     <title>   |<link1>,<link2>,<link3>,<link4>, ... , <linkN>
         *     
         * Another record composed by the linked page, the page rank of the source page 
         * and the total amount of out links of the source page:
         *  
         *     <link>    <page-rank>    <total-links>
         */

        String[] line = value.toString().split("\t");
        // dangling users will has length = 2, since they don't refer to any pages
        if (line.length == 3) {
            String page = line[0];
            String pageRank = line[1];
            String links = line[2];

            String[] allOtherPages = links.split(",");
            for (String otherPage : allOtherPages) {
                Text pageRankWithTotalLinks = new Text(pageRank + "\t" + allOtherPages.length);
                context.write(new Text(otherPage), pageRankWithTotalLinks);
            }

            // put the original links so the reducer is able to produce the correct output
            context.write(new Text(page), new Text(PageRank.LINKS_SEPARATOR + links));
        }
    }
    
}

