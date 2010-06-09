/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package moldyn;

public class Particle {

  public var xcoord: Double, ycoord: Double, zcoord: Double;
  public var xvelocity: Double, yvelocity: Double, zvelocity: Double;
  public var xforce: Double, yforce: Double, zforce: Double;

  public def this(xcoord: Double, ycoord: Double, zcoord: Double, 
                  xvelocity: Double, yvelocity: Double, zvelocity: Double, 
                  xforce: Double, yforce: Double, zforce: Double) {
    this.xcoord = xcoord;
    this.ycoord = ycoord;
    this.zcoord = zcoord;
    this.xvelocity = xvelocity;
    this.yvelocity = yvelocity;
    this.zvelocity = zvelocity;
    this.xforce = xforce;
    this.yforce = yforce;
    this.zforce = zforce;
  }

  public def domove(side: Double) {
    xcoord = xcoord + xvelocity + xforce;
    ycoord = ycoord + yvelocity + yforce;
    zcoord = zcoord + zvelocity + zforce;

    if (xcoord < 0) { xcoord = xcoord + side; }
    if (xcoord > side) { xcoord = xcoord - side; }
    if (ycoord < 0) { ycoord = ycoord + side; }
    if (ycoord > side) { ycoord = ycoord - side; }
    if (zcoord < 0) { zcoord = zcoord + side; }
    if (zcoord > side) { zcoord = zcoord - side; }

    xvelocity = xvelocity + xforce;
    yvelocity = yvelocity + yforce;
    zvelocity = zvelocity + zforce;

    xforce = 0.0;
    yforce = 0.0;
    zforce = 0.0;
  }

  public def force(side: Double, rcoff: Double, mdsize: Int, x: Int, md1: md!) {
    var sideh: Double;
    var rcoffs: Double;

    var xx: Double, yy: Double, zz: Double, xi: Double, yi: Double, zi: Double;
    var fxi: Double, fyi: Double, fzi: Double;
    var rd: Double, rrd: Double, rrd2: Double, rrd3: Double, rrd4: Double;
    var rrd6: Double, rrd7: Double, r148: Double;
    var forcex: Double, forcey: Double, forcez: Double;

    // var i: Int;

    sideh = 0.5*side;
    rcoffs = rcoff*rcoff;

    xi = xcoord;
    yi = ycoord;
    zi = zcoord;
    fxi = 0.0;
    fyi = 0.0;
    fzi = 0.0;

    // for (i = x + 1; i < mdsize; i++) {
    for ((i) in x+1..mdsize-1) {
      xx = xi - md1.one(i).xcoord;
      yy = yi - md1.one(i).ycoord;
      zz = zi - md1.one(i).zcoord;

      if (xx < (-sideh)) { xx = xx + side; }
      if (xx > (sideh))  { xx = xx - side; }
      if (yy < (-sideh)) { yy = yy + side; }
      if (yy > (sideh))  { yy = yy - side; }
      if (zz < (-sideh)) { zz = zz + side; }
      if (zz > (sideh))  { zz = zz - side; }

      rd = xx*xx + yy*yy + zz*zz;

      if (rd <= rcoffs) {
        rrd = 1.0/rd;
        rrd2 = rrd*rrd;
        rrd3 = rrd2*rrd;
        rrd4 = rrd2*rrd2;
        rrd6 = rrd2*rrd4;
        rrd7 = rrd6*rrd;
        md1.epot = md1.epot + (rrd6 - rrd3);
        r148 = rrd7 - 0.5*rrd4;
        md1.vir = md1.vir - rd*r148;
        forcex = xx * r148;
        fxi = fxi + forcex;
        md1.one(i).xforce = md1.one(i).xforce - forcex;
        forcey = yy * r148;
        fyi = fyi + forcey;
        md1.one(i).yforce = md1.one(i).yforce - forcey;
        forcez = zz * r148;
        fzi = fzi + forcez;
        md1.one(i).zforce = md1.one(i).zforce - forcez;
        md1.interactions++;
      }
    }

    xforce = xforce + fxi;
    yforce = yforce + fyi;
    zforce = zforce + fzi;
  }

  public def mkekin(hsq2: Double) {
    val sumt: Double;

    xforce = xforce * hsq2;
    yforce = yforce * hsq2;
    zforce = zforce * hsq2;

    xvelocity = xvelocity + xforce;
    yvelocity = yvelocity + yforce;
    zvelocity = zvelocity + zforce;

    sumt = (xvelocity*xvelocity)+(yvelocity*yvelocity)+(zvelocity*zvelocity);
    return sumt;
  }

  public def velavg(vaverh: Double, h: Double, md1: md!) {
    val velt: Double;
    val sq: Double;

    sq = Math.sqrt(xvelocity*xvelocity + yvelocity*yvelocity + 
                   zvelocity*zvelocity);

    if (sq > vaverh) { md1.count = md1.count + 1.0; }

    velt = sq;
    return velt;
  }

  public def dscal(sc: Double, incx: Int) {
    xvelocity = xvelocity * sc;
    yvelocity = yvelocity * sc;
    zvelocity = zvelocity * sc;
  }
}
