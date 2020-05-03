/*
 * Swing version.
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;  //Vector
import java.lang.*; //Math

/*
 * Displays a framed area.  When the user clicks within
 * the area, this program displays a filled rectangle
 * and a string indicating the coordinates where the
 * click occurred.
 */

public class RectangleFind extends JApplet{
    JLabel label;
    JPanel buttonPane;
    JButton b1;
    JButton b2;
    JButton b3;

    ConvexHull polygon;
    RectangleArea rectangleArea;
    MyListener myListener;

    //Called only when this is run as an applet.
    public void init() {
        buildUI(getContentPane());
    }

    void buildUI(Container container) {
        container.setLayout(new BoxLayout(container,
                BoxLayout.Y_AXIS));

        label = new JLabel("Click within the framed area.");
        container.add(label);

        polygon = new ConvexHull();

        rectangleArea = new RectangleArea(this);

        myListener = new MyListener(this);
        rectangleArea.addMouseListener(myListener);

        //  addMouseListener(this);

        container.add(rectangleArea);

        buttonPane = new JPanel();
        //buttonPane.setPreferredSize(new Dimension(100, 25));
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        b1 = new JButton("Reset");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                polygon = new ConvexHull();
                rectangleArea.repaint();
                updateLabel(0);
            }
        });
        b2 = new JButton("Find Largest Rectangle");
        //b2.setEnabled(false );
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //polygon = new ConvexHull();
                polygon.computeLargestRectangle();
                rectangleArea.displayedRect = 6;
                polygon.status = 16;
                rectangleArea.repaint();
                //updateLabel(16);
                // label.setText("16");
            }
        });

        b3 = new JButton("Step");
        b3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (polygon.changed){
                    //       System.out.println("caught");
                    polygon.computeLargestRectangle();
                }
                //System.out.println(polygon.changed);
                rectangleArea.displayedRect++;
                if (rectangleArea.displayedRect == 7){
                    rectangleArea.displayedRect = 0;
                }
                polygon.status = rectangleArea.displayedRect + 10;
                rectangleArea.repaint();
            }
        });

        buttonPane.add(b1);
        buttonPane.add(Box.createHorizontalStrut(10)); //createHorizontalGlue();
        buttonPane.add(b2);
        buttonPane.add(b3);

        container.add(buttonPane);

        //Align the left edges of the components.
        rectangleArea.setAlignmentX(LEFT_ALIGNMENT);
        label.setAlignmentX(LEFT_ALIGNMENT); //unnecessary, but doesn't hurt
        buttonPane.setAlignmentX(LEFT_ALIGNMENT);
    }
    /* display a predefined message for a given msg state */
    public void updateLabel(int msg) {
        if (msg == 0){
            label.setText("Click within the framed area to add points");
        }
        else if (msg == 1){
            label.setText("Point added, click to add more points");
        }
        else if (msg == 2){
            label.setText("point added to polygon");
        }
        else if (msg == 3){
            label.setText("point inside not added");
        }
        else if (msg == 10){
            label.setText("Largest rectangle with corners on A and C only");
        }
        else if (msg == 11){
            label.setText("Largest rectangle with corners on B and D only");
        }
        else if (msg == 12){
            label.setText("Largest rectangle with corners on A,B and C");
        }
        else if (msg == 13){
            label.setText("Largest rectangle with corners on A,B and D");
        }
        else if (msg == 14){
            label.setText("Largest rectangle with corners on A,C and D");
        }
        else if (msg == 15){
            label.setText("Largest rectangle with corners on B,C and D");
        }
        else if (msg == 16){
            label.setText("Largest rectangle in polygon");
        }
        else {
            label.setText("Click to add points");
        }

    }

    //Called only when this is run as an application.
    public static void main(String[] args) {
        JFrame f = new JFrame("RectangleFind");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        RectangleFind controller = new RectangleFind();
        controller.buildUI(f.getContentPane());
        f.pack();
        f.setVisible(true);
    }


}

class MyListener extends MouseInputAdapter{
    RectangleFind rf;

    public MyListener(RectangleFind rf){
        this.rf = rf;
    }
    public void mousePressed(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        GeomPoint p = new GeomPoint(x,y);

        // this.polygon.add(p);
        if (this.rf.polygon.size() <2){
            this.rf.polygon.add(p);
            this.rf.polygon.status = 1;
            this.rf.polygon.changed = true;
        }

        else if (this.rf.polygon.size() == 2){
            GeomPoint ha = (GeomPoint)this.rf.polygon.elementAt(0);
            GeomPoint hb = (GeomPoint)this.rf.polygon.elementAt(1);
            if (this.rf.polygon.onLeft(ha, hb, p)){
                this.rf.polygon.add(p);
                this.rf.polygon.status = 2;
                this.rf.polygon.changed = true;
            }
            else{
                this.rf.polygon.insertElementAt(p, 1);
                this.rf.polygon.status = 2;
            }
        }
        else{
            if (this.rf.polygon.addPointToHull(p)){
                this.rf.polygon.status = 2;
            }
            else{
                this.rf.polygon.status = 3;
            }
        }

        this.rf.rectangleArea.repaint();
        //this.rectangleArea.paintComponent(g);

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

}


class RectangleArea extends JPanel {

    // ConvexHull hull = new ConvexHull();
    RectangleFind controller;
    Dimension preferredSize = new Dimension(600,450);
    int rectWidth = 50;
    int rectHeight = 50;
    public int displayedRect = 6;

    public RectangleArea(RectangleFind controller) {
        this.controller = controller;

        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder
                (raisedBevel, loweredBevel);
        setBorder(compound);

    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);  //paint background

        final ConvexHull hull = this.controller.polygon;

        GeomPoint point = null;
        GeomPoint prevPoint = null;

        for(int i=0; i<hull.size(); i++){

            if(i==0){
                prevPoint = (GeomPoint)hull.elementAt(hull.size()-1);
            }

            point = (GeomPoint)hull.elementAt(i);

            g.setColor(Color.black);
            g.fillOval(point.x-2, point.y-2, 5, 5);

            if(prevPoint != null){
                g.fillOval(prevPoint.x-2, prevPoint.y-2,5,5);
                g.drawLine(point.x, point.y, prevPoint.x, prevPoint.y);
            }
            prevPoint = point;
        }
        if(hull.rectp !=null){
            if (displayedRect ==6){
                g.setColor(Color.red);
            }
            else{
                g.setColor(Color.magenta);
            }
            Rectangle lr = (Rectangle)hull.RectList.elementAt(displayedRect);

            g.drawRect(lr.x,lr.y, lr.width, lr.height);
            g.fillRect(lr.x,lr.y, lr.width, lr.height);
        }

        controller.updateLabel(hull.status);

    }
}

class GeomPoint extends Point {

    public GeomPoint(int ptx, int pty){
        this.x = ptx;
        this.y = pty;
    }

    int min(int a, int b){
        if(a<=b) return a; else return b;
    }

    int max(int a, int b){
        if (a>=b) return a; else return b;
    }
}


class GeomEdge{

    int xmin, xmax; /* horiz, +x is right */
    int ymin, ymax; /* vertical, +y is down */
    double m,b; /* y = mx + b */
    boolean isTop, isRight; /* position of edge w.r.t. hull */

    public GeomEdge(GeomPoint p, GeomPoint q){
        this.xmin = p.min(p.x, q.x);
        this.xmax = p.max(p.x, q.x);
        this.ymin = p.min(p.y, q.y);
        this.ymax = p.max(p.y, q.y);
        this.m = ((double)(q.y-p.y))/((double)(q.x-p.x));
        this.b = p.y - m*(p.x);
        this.isTop = p.x > q.x; //edge from right to left (ccw)
        this.isRight = p.y > q.y; //edge from bottom to top (ccw)
    }
}


class ConvexHull extends Vector {

    int status;
    private int start, stop; //tangents for iterative convex hull
    private int xmin,xmax,ymin,ymax;  //position of hull
    int yxmax; //y coord of xmax
    GeomPoint rectp;
    int recth, rectw;
    boolean changed;

    /* largest rectangles with corners on AC, BD, ABC, ABD, ACD, BCD */
    Vector RectList;

    /* fixed aspect ratio */
    private boolean fixed;
    private int fixedX, fixedY;

    public ConvexHull() {
        this.fixed = false;
        this.fixedX = 1;
        this.fixedY = 1;
        RectList = new Vector();
    }

    /* position of point w.r.t. hull edge
     * sign of twice the area of triangle abc
     */
    boolean onLeft(GeomPoint a, GeomPoint b, GeomPoint c){
        int area = (b.x -a.x)*(c.y - a.y) - (c.x - a.x)*(b.y - a.y);
        return (area<0);
    }

    /* check if point is outside
     * true is point is on right of all vertices
     * finds tangents if point is outside
     */
    boolean pointOutside(GeomPoint p){//, int start, int stop){

        boolean ptIn = true, currIn, prevIn = true;

        GeomPoint a = (GeomPoint)this.elementAt(0);
        GeomPoint b;

        for(int i=0; i<this.size(); i++){

            b = (GeomPoint)this.elementAt((i+1)%this.size());
            currIn = onLeft(a, b, p);
            ptIn = ptIn && currIn;
            a = b;

            if(prevIn && !currIn){ start = i;} /* next point outside, 1st tangent found */
            if(!prevIn && currIn){ stop = i;}  /* 2nd tangent */
            prevIn = currIn;

        }
        return !ptIn;
    }

    /* check if point is outside, insert it, maintaining general position */
    boolean addPointToHull(GeomPoint p) {

        /* index of tangents */
        start=0;
        stop=0;

        if(!pointOutside(p)){
            return false;
        }

        /* insert point */
        int numRemove;

        if (stop > start){
            numRemove = stop-start-1;
            if (numRemove>0){
                this.removeRange(start+1, stop);
            }
            this.insertElementAt(p, start+1); //insertElmentAt(p, start+1);
        }
        else{
            numRemove = stop+this.size()-start-1;
            if (numRemove > 0){
                if (start+1 < this.size()){
                    this.removeRange(start+1, this.size());
                }
                if(stop-1 >= 0){
                    this.removeRange(0, stop);
                }
            }
            this.add(p);

        }
        System.out.println("changing");
        this.changed = true;
        return true;
    } //addPointToHull

    /* compute edge list
     * set xmin, xmax
     * used to find largest rectangle by scanning horizontally
     */
    Vector computeEdgeList(){
        Vector l = new Vector();
        GeomPoint a,b;
        GeomEdge e;
        a = (GeomPoint)this.elementAt(this.size()-1);
        for(int i=0; i<this.size(); i++){
            b = (GeomPoint)this.elementAt(i);
            //b = (GeomPoint)this.elementAt(i+1);

            if (i==0){
                this.xmin = a.x;
                this.xmax = a.x;
                this.ymin = a.y;
                this.ymax = a.y;
            }
            else{
                if (a.x < this.xmin){
                    this.xmin = a.x;
                }
                if (a.x > this.xmax){
                    this.xmax  = a.x;
                    this.yxmax = a.y;
                }
                if (a.y < this.ymin){
                    this.ymin = a.y;
                }
                if (a.y > this.ymax){
                    this.ymax  = a.y;
                }
            }
            e = new GeomEdge(a,b);
            l.add(e);
            a = b;
        } //for
        // b = (GeomPoint)this.elementAt(this.size()-1);
        // a = (GeomPoint)this.elementAt(0);
        // e = new GeomEdge(b,a);
        // l.add(e);
        return l;
    }

    /* compute y intersection with an edge
     * first pixel completely inside
     * ceil function if edge is on top, floor otherwise
     * (+y is down)
     */
    int yIntersect(int xi, GeomEdge e){

        int y;
        double yfirst = (e.m) * (xi-0.5) + e.b;
        double ylast = (e.m) * (xi+0.5) + e.b;

        if (!e.isTop){
            y = (int)Math.floor(Math.min(yfirst, ylast));
        }
        else {
            y = (int)Math.ceil(Math.max(yfirst, ylast));
        }
        return y;
    }

    /* find largest pixel completely inside
     * look through all edges for intersection
     */
    int xIntersect(int y, Vector l){
        int x=0;
        double x0=0, x1=0;
        for(int i=0; i<this.size(); i++){
            GeomEdge e = (GeomEdge)l.elementAt(i);
            if (e.isRight && e.ymin <= y && e.ymax >= y){
                x0 = (double)(y+0.5 - e.b)/e.m;
                x1 = (double)(y-0.5 - e.b)/e.m;
            }
        }
        x = (int)Math.floor(Math.min(x0,x1));
        //System.out.println("xIntersect, x is " + x);
        return x;
    }

    GeomEdge findEdge(int x, boolean isTop, Vector l){
        GeomEdge e,emax=(GeomEdge)l.elementAt(0);
        //int count = 0;
        for (int i=0; i<this.size(); i++){
            e = (GeomEdge)l.elementAt(i);
            if (e.xmin == x){
                //count++;
                //if (count == 1){
                //    emax = e;
                //}
                //else{
                if (e.xmax != e.xmin){
                    if ((e.isTop && isTop)||(!e.isTop && !isTop)){
                        emax = e;
                    }
                }
            }

        }
        return emax;
    }

    /* compute 3 top and bottom 3 corner rectangle for each xi
     * find largest 2 corner rectangle
     */
    int computeLargestRectangle(){

        this.changed = false;
        Vector edgeList = computeEdgeList();
        this.RectList = new Vector();

        GeomEdge top, bottom;
        int ymax, ymin, xright, xlo, xhi;
        int area, maxArea = 0;
        int maxAreaAC=0, maxAreaBD=0, maxAreaABC=0, maxAreaABD=0, maxAreaACD=0, maxAreaBCD=0;
        int width, height, maxh=0, maxw=0;

        /* all 2-corner and 3-corner largest rectangles */
        int aAC=0,aBD=0,aABC=0,aABD=0,aACD=0,aBCD=0;
        GeomPoint pAC, pBD, pABC, pABD, pACD, pBCD;
        int hAC=0,wAC=0,hBD=0,wBD=0,hABC=0,wABC=0,hABD=0,wABD=0,hACD=0,wACD=0,hBCD=0,wBCD=0;
        boolean onA, onB, onC, onD;

        GeomPoint maxp = new GeomPoint(0,0);
        pAC = maxp; pBD = maxp; pABC = maxp; pABD = maxp; pACD = maxp; pBCD = maxp;

        Vector xint = new Vector();

        for(int i=0;i<this.ymax;i++){
            int x = xIntersect(i,edgeList);
            GeomPoint px = new GeomPoint(x,i);
            xint.add(px);
        }
        //find first top and bottom edges
        top = findEdge(this.xmin, true, edgeList);
        bottom = findEdge(this.xmin, false, edgeList);

        //scan for rectangle left position
        for(int xi=this.xmin; xi<this.xmax;xi++){

            ymin = yIntersect(xi, top);
            ymax = yIntersect(xi, bottom);

            for(int ylo = ymax;ylo>=ymin;ylo--){//ylo from to to bottom

                for(int yhi = ymin; yhi<=ymax; yhi++){

                    if (yhi>ylo){

                        onA = (yhi == ymax && !bottom.isRight);
                        onD = (ylo == ymin && !top.isRight);

                        xlo = (int)((GeomPoint)xint.elementAt(ylo)).x;//xIntersect(ylo,edgeList);
                        xhi = (int)((GeomPoint)xint.elementAt(yhi)).x;//xIntersect(yhi,edgeList);

                        xright = maxp.min(xlo,xhi);
                        onC = (xright == xlo && this.yxmax >= ylo);
                        onB = (xright == xhi && this.yxmax <= yhi);

                        height = yhi-ylo;
                        width = xright - xi;

                        if (!this.fixed){
                        }//!fixed
                        else{
                            int fixedWidth = (int)Math.ceil( ((double) height*this.fixedX)/((double)this.fixedY));
                            if (fixedWidth <= width){
                                width = fixedWidth;
                            }
                            else{
                                width = 0;
                            }
                        }
                        area = width * height;
                        //AC
                        if (onA && onC && !onB && !onD){
                            if (area > aAC){
                                aAC = area;
                                pAC = new GeomPoint(xi, ylo);
                                hAC = height;
                                wAC = width;
                            }
                        }
                        //BD
                        if (onB && onD && !onA && !onC){
                            if (area > aBD){
                                aBD = area;
                                pBD = new GeomPoint(xi, ylo);
                                hBD = height;
                                wBD = width;
                            }
                        }
                        //ABC
                        if (onA && onB && onC){
                            if (area > aABC){
                                aABC = area;
                                pABC = new GeomPoint(xi, ylo);
                                hABC = height;
                                wABC = width;
                            }
                        }
                        //ABD
                        if (onA && onB && onD){
                            if (area > aABD){
                                aABD = area;
                                pABD = new GeomPoint(xi, ylo);
                                hABD = height;
                                wABD = width;
                            }
                        }
                        //ACD
                        if (onA && onC && onD){
                            if (area > aACD){
                                aACD = area;
                                pACD = new GeomPoint(xi, ylo);
                                hACD = height;
                                wACD = width;
                            }
                        }
                        //BCD
                        if (onB && onC && onD){
                            if (area > aBCD){
                                aBCD = area;
                                pBCD = new GeomPoint(xi, ylo);
                                hBCD = height;
                                wBCD = width;
                            }
                        }

                        if(area>maxArea){
                            maxArea = area;
                            maxp = new GeomPoint(xi, ylo);
                            maxw = width;
                            maxh = height;
                            // System.out.println(onA + " " + onB + " " + onC + " " + onD);
                        }
                    }//yhi > ylo
                }//for yhi
            }//for ylo
            if (xi == top.xmax){
                top = findEdge(xi,  true, edgeList);
            }
            if(xi == bottom.xmax){
                bottom = findEdge(xi, false, edgeList);
            }
        }//xi
        this.rectp = maxp;
        this.recth = maxh;
        this.rectw = maxw;

        this.RectList.add(new Rectangle(pAC.x, pAC.y, wAC, hAC));
        this.RectList.add(new Rectangle(pBD.x, pBD.y, wBD, hBD));
        this.RectList.add(new Rectangle(pABC.x, pABC.y, wABC, hABC));
        this.RectList.add(new Rectangle(pABD.x, pABD.y, wABD, hABD));
        this.RectList.add(new Rectangle(pACD.x, pACD.y, wACD, hACD));
        this.RectList.add(new Rectangle(pBCD.x, pBCD.y, wBCD, hBCD));
        this.RectList.add(new Rectangle(maxp.x, maxp.y, maxw, maxh));
        return 0;

    }


}
